package com.example.dears;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class ChatActivity extends AppCompatActivity {
    private String age;
    private String petType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        final Button button = findViewById(R.id.Generate);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateTextView();
            }
        });
    }

    public void updateTextView() {
        TextView textView = findViewById(R.id.LLMResults);
        LLMInference llm = new LLMInference(this);

        // put the llm call on a thread so it doesn't hog all of the resources
        new Thread(() -> {
            try {
                llm.generateJournalEntry("young", "cat", 80, 60, 70, new LLMInference.LLMCallback() {
                    @Override
                    public void onComplete(String llmResult) {
                        runOnUiThread(() -> {
                            try {
                                String processedResult = llmResult.substring(7, llmResult.length() - 4);
                                JSONObject json = new JSONObject(processedResult);
                                String mood = json.getString("mood");
                                String summary = json.getString("summary");
                                String entryText = json.getString("summary");

                                textView.setText(processedResult);

                                // Send to backend on background thread
                                new Thread(() -> {
                                    llm.sendToBackend(new Date(), entryText, mood, summary,
                                        new LLMInference.CreateEntryCallback() {
                                            @Override
                                            public void onSuccess(JSONObject response) {
                                                runOnUiThread(() ->
                                                    Toast.makeText(getApplicationContext(),"Saved", Toast.LENGTH_SHORT).show()
                                                );
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                runOnUiThread(() ->
                                                    Toast.makeText(getApplicationContext(),"Failed to save", Toast.LENGTH_SHORT).show()
                                                );
                                            }
                                    });
                                }).start();

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),"Error parsing LLM output", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(() ->
                            Toast.makeText(getApplicationContext(),"Error generating response", Toast.LENGTH_SHORT).show()
                        );
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                    Toast.makeText(getApplicationContext(), "Error running inference", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}