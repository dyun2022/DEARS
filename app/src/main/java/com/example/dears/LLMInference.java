package com.example.dears;

import android.content.Context;

import com.google.mediapipe.tasks.genai.llminference.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
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


    public void generateJournalEntry(String age, String type, int happiness, int hunger, int sleep, LLMCallback callback) {
        String prompt = "In JSON format with the fields \"date\", \"mood\", and \"summary\"," +
                "where date is MM-DD-YYYY format and mood is an integer out of 100, " +
                "generate a journal entry in the voice of a " + age + " " + type +
                " with happiness level: " + happiness + ", hunger satisfaction level: " + hunger + ", and amount of sleep gotten " + sleep +
                ". If hunger and sleep levels are low, make it sound angrier. If happiness is high, make it sound cheerful.";

        // send prompt to LLM
        callLLM(prompt, callback);
    }

    public void respondToChat(String age, String type, int happiness, int hunger, int sleep, String input, LLMCallback callback) {
        String prompt = "In JSON format with the field \"response\", " +
                "generate a response to the input, " + input +  " in the voice of a " + age + " " + type +
                " with happiness level: " + happiness + ", hunger satisfaction level: " + hunger + ", and amount of sleep gotten " + sleep +
                ". If hunger, sleep, or happiness levels are low, make it sound angrier. If happiness is high, make it sound cheerful.";

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
}
