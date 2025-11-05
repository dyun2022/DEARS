package com.example.dears;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.graphics.Path;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dears.data.model.Pet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
//import com.example.dears.data.model.Pet;

public class ChatActivity extends AppCompatActivity {
    private String age;
    private String petType = null;
    private int petID = -1;
    private int userId;
    private Pet pet;
    /// should be able to do pet.name after and just use dot operator for all the values

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

        Intent intent = getIntent();
        pet = (Pet) intent.getSerializableExtra("pet");
        userId = intent.getIntExtra("userId", -1);


        if (userId != -1) {

        } else {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
        }

        ImageView petPicture = findViewById(R.id.petPicture);

        //check pet type
//        if (petType.equalsIgnoreCase("bear")) {
//            //check age stage
//            if ()
//        } else {
//
//        }

        TextView textView = findViewById(R.id.LLMResults);

//        final Button button = findViewById(R.id.Generate);
//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                updateTextView("hi how are you");
//            }
//        });

        final Button hiButton = findViewById(R.id.chatHello);
        hiButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                textView.setText("Loading...");
                updateTextView(hiButton.getText().toString());
            }
        });

        final Button greetingButton = findViewById(R.id.chatGreeting);
        greetingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                textView.setText("Loading...");
                updateTextView(greetingButton.getText().toString());
            }
        });

        final Button jokeButton = findViewById(R.id.chatJoke);
        jokeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                textView.setText("Loading...");
                updateTextView(jokeButton.getText().toString());
            }
        });

        final Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener( v -> {
            Intent mainIntent = new Intent(ChatActivity.this, PetHomeActivity.class);
            mainIntent.putExtra("pet", pet);
            mainIntent.putExtra("userId", userId);
            startActivity(mainIntent);
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

//                                textView.setText(processedResult);
                                textView.setText(response);

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

