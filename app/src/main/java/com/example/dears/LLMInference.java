package com.example.dears;

import android.content.Context;
import android.util.Log;

import com.google.mediapipe.tasks.genai.llminference.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;

public class LLMInference {
    private String modelPath = "/data/local/tmp/llm/gemma3-1b-it-int4.task";
    private Context context;
    private static final String BASE_URL = "http://10.0.2.2:8080";

    public LLMInference(Context context) {
        this.context = context;
        ensureModelCopied();
        this.modelPath = new File(context.getFilesDir(), "llm/gemma3-1b-it-int4.task").getAbsolutePath();
    }

    public void callLLM(String prompt, LLMCallback callback) {
        new Thread(() -> {
            try {
                LlmInference.LlmInferenceOptions taskOptions = LlmInference.LlmInferenceOptions.builder()
                        .setModelPath(modelPath)
                        .setMaxTokens(512)
                        .build();

                LlmInference llmInference = LlmInference.createFromOptions(context, taskOptions);

                String result = llmInference.generateResponse(prompt);

                if (result != null) {
                    result = result.trim();
                    callback.onComplete(result);
                } else {
                    callback.onError(new Exception("LLM returned null"));
                }

            } catch (Exception e) {
                callback.onError(e);
            }
        }).start();
    }


    public void generateJournalEntry(String age, String type, double happiness, double hunger, double sleep, LLMCallback callback) {
        String prompt = "In strict JSON format with the fields \"date\", \"mood\", and \"summary\"," +
                "where date is MM-DD-YYYY format and mood is an integer out of 100, " +
                "generate a journal entry in the voice of a " + age + " " + type +
                " with happiness level percent: " + happiness + ", hunger satisfaction level percent: " + hunger + ", and energy level percent: " + sleep +
                " If hunger and sleep levels are < 0.5, make it sound angrier."
                + " If happiness > 0.5, make it sound cheerful." +
                " You are to output ONLY valid JSON (no explanations, no markdown). " +
                "Summary must be ONE sentence and no more than 20 words." +
                "\nUse the following fields exactly:\n" +
                "{\n" +
                "  \"date\": \"MM-DD-YYYY\",\n" +
                "  \"mood\": <integer from 0 to 100>,\n" +
                "  \"summary\": \"<string>\"\n" +
                "}\n";

        // send prompt to LLM
        callLLM(prompt, callback);
    }

    public void respondToChat(String age, String type, double happiness, double hunger, double sleep, String input, LLMCallback callback) {
        String prompt = "Respond ONLY in strict JSON format with only the field \"response\" where the response is a String." +
                "Do not include any text before or after the JSON. Keep the response short (less than 25 words). " +
                "Generate a response to the input, " + input +  " in the voice of a " + age + " " + type +
                " with happiness level: " + happiness + ", hunger satisfaction level: " + hunger + ", and amount of sleep gotten " + sleep +
                ". If hunger, sleep, or happiness levels are low, make it sound angrier. If happiness is high, make it sound cheerful."
        + " You are to output ONLY valid JSON (no explanations, no markdown)." +
                "\nUse the following fields exactly:\n" +
                "{\n" +
                "  \"response\": \"<string>\"\n" +
                "}\n";

        // send prompt to LLM
        callLLM(prompt, callback);
    }

    public void sendToBackend(Date date, String entryText, String mood, String summary, CreateEntryCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + "/api/journal/" + date);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8"); // Add charset
                conn.setDoOutput(true);

                JSONObject payload = new JSONObject();
                payload.put("text", entryText);
                payload.put("mood", mood);
                payload.put("summary", summary);

                // Write with explicit UTF-8 encoding
                OutputStream os = conn.getOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(os, "UTF-8");
                writer.write(payload.toString());
                writer.flush();
                writer.close();

                // Read response with UTF-8
                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), "UTF-8")); // Add UTF-8
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    callback.onSuccess(jsonResponse);
                }

                conn.disconnect();
            } catch (Exception e) {
                callback.onError(e);
            }
        }).start();
    }

    public interface CreateEntryCallback {
        void onSuccess(JSONObject response);
        void onError(Exception e);
    }

    public interface LLMCallback {
        void onComplete(String result);
        void onError(Exception e);
    }

    private void ensureModelCopied() {
        File llmDir = new File(context.getFilesDir(), "llm");
        if (!llmDir.exists()) llmDir.mkdirs();

        File modelFile = new File(llmDir, "gemma3-1b-it-int4.task");
        if (modelFile.exists()) return;

        try (InputStream in = context.getAssets().open("llm/gemma3-1b-it-int4.task");
             OutputStream out = new FileOutputStream(modelFile)) {
            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
            Log.d("LLMInference", "Model copied to " + modelFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e("LLMInference", "Failed to copy model", e);
        }
    }
}
