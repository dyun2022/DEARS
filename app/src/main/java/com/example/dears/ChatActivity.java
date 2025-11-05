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

//        final Button button = findViewById(R.id.Generate);
//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                updateTextView();
//            }
//        });

        final Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateTextView(button1.getText().toString());
            }
        });

        final Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateTextView(button2.getText().toString());
            }
        });

        final Button button3 = findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateTextView(button3.getText().toString());
            }
        });
    }

    public void updateTextView(String prompt) {
        TextView textView = findViewById(R.id.LLMResults);
        LLMInference llm = new LLMInference(this);

        // put the llm call on a thread so it doesn't hog all of the resources
        new Thread(() -> {
            try {
                llm.respondToChat("baby", "deer", 80, 60, 70, prompt, new LLMInference.LLMCallback() {
                    @Override
                    public void onComplete(String llmResult) {
                        System.out.println(llmResult);
                        runOnUiThread(() -> {
                            try {
                                String processedResult = llmResult.replace("```json", "").replace("```", "").trim();
                                JSONObject json = new JSONObject(processedResult);
                                String response = json.getString("response");

                                textView.setText(processedResult);

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