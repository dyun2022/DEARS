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
    private String modelPath;
    private Context context;
    private static final String BASE_URL = "http://10.0.2.2:8080";
    private static final String TAG = "LLMInference";

    public LLMInference(Context context) {
        Log.e(TAG, "========== CONSTRUCTOR STARTING ==========");

        if (context == null) {
            Log.e(TAG, "ERROR: Context is null!");
            throw new IllegalArgumentException("Context cannot be null");
        }

        Log.e(TAG, "Context is valid");
        this.context = context;

        Log.e(TAG, "About to call ensureModelCopied()");
        ensureModelCopied();
        Log.e(TAG, "ensureModelCopied() completed");

        this.modelPath = new File(context.getFilesDir(), "llm/gemma3-1b-it-int4.task").getAbsolutePath();
        Log.e(TAG, "Final modelPath = " + modelPath);

        File modelFile = new File(modelPath);
        Log.e(TAG, "Model exists? " + modelFile.exists());
        Log.e(TAG, "Model size: " + modelFile.length() + " bytes");
        Log.e(TAG, "Model can read? " + modelFile.canRead());

        if (!modelFile.exists()) {
            Log.e(TAG, "CRITICAL: MODEL FILE DOES NOT EXIST!");
        } else if (modelFile.length() == 0) {
            Log.e(TAG, "CRITICAL: MODEL FILE IS EMPTY!");
        } else {
            Log.e(TAG, "Model file looks OK");
        }

        Log.e(TAG, "========== CONSTRUCTOR FINISHED ==========");
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
                Log.e("LLMInference", "callLLM failed: " + e.getMessage(), e);
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
        Log.e(TAG, "--- ensureModelCopied START ---");

        if (context == null) {
            Log.e(TAG, "Context is null in ensureModelCopied");
            return;
        }

        File llmDir = new File(context.getFilesDir(), "llm");
        Log.e(TAG, "llmDir path = " + llmDir.getAbsolutePath());
        Log.e(TAG, "llmDir exists? " + llmDir.exists());

        if (!llmDir.exists()) {
            boolean created = llmDir.mkdirs();
            Log.e(TAG, "Created llmDir: " + created);
        }

        File modelFile = new File(llmDir, "gemma3-1b-it-int4.task");
        Log.e(TAG, "modelFile path = " + modelFile.getAbsolutePath());
        Log.e(TAG, "modelFile exists before copy? " + modelFile.exists());

        if (modelFile.exists()) {
            Log.e(TAG, "Model already exists! Size: " + modelFile.length());
            Log.e(TAG, "--- ensureModelCopied END (skipped copy) ---");
            return;
        }

        Log.e(TAG, "Model doesn't exist, starting copy from assets...");

        InputStream in = null;
        OutputStream out = null;
        try {
            Log.e(TAG, "Opening asset: llm/gemma3-1b-it-int4.task");
            in = context.getAssets().open("llm/gemma3-1b-it-int4.task");
            Log.e(TAG, "Asset opened successfully");

            Log.e(TAG, "Creating FileOutputStream");
            out = new FileOutputStream(modelFile);
            Log.e(TAG, "FileOutputStream created");

            byte[] buf = new byte[8192];
            int len;
            long totalBytes = 0;
            int chunks = 0;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
                totalBytes += len;
                chunks++;

                // Log progress every 10MB
                if (chunks % 1280 == 0) {
                    Log.e(TAG, "Copied " + (totalBytes / 1024 / 1024) + " MB so far...");
                }
            }

            Log.e(TAG, "Copy complete! Total bytes: " + totalBytes);
            Log.e(TAG, "Total MB: " + (totalBytes / 1024 / 1024));
            Log.e(TAG, "Final file size: " + modelFile.length());

        } catch (IOException e) {
            Log.e(TAG, "EXCEPTION during copy: " + e.getMessage(), e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                    Log.e(TAG, "InputStream closed");
                }
                if (out != null) {
                    out.close();
                    Log.e(TAG, "OutputStream closed");
                }
            } catch (IOException e) {
                Log.e(TAG, "Exception closing streams", e);
            }
        }

        Log.e(TAG, "--- ensureModelCopied END ---");
    }
}
