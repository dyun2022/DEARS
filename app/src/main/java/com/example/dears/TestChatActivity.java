
package com.example.dears;

public class TestChatActivity extends ChatActivity {
    @Override
    protected LLMInference createLLMInference() {
        // Returning null prevents the JNI-backed LLM from being loaded during unit tests.
        return null;
    }
}
