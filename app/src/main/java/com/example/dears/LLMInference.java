package com.example.dears;

import android.content.Context;

import com.google.mediapipe.tasks.genai.llminference.*;

public class LLMInference {
    private String modelPath = "/data/local/tmp/llm/gemma3-1b-it-int4.task";
    private Context context;

    public LLMInference(Context context) {
        this.context = context;
    }

    public String callLLM(String prompt) {
        // Set the configuration options for the LLM Inference task (from https://ai.google.dev/edge/mediapipe/solutions/genai/llm_inference/android)
        LlmInference.LlmInferenceOptions taskOptions = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelPath)
                .setMaxTopK(64)
                .build();

        // Create an instance of the LLM Inference task
        LlmInference llmInference = LlmInference.createFromOptions(context, taskOptions);

        // send prompt to LLM
        String result = llmInference.generateResponse(prompt);
        return result;
    }
}
