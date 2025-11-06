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

        final Button button = findViewById(R.id.Generate);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateTextView("hi how are you");
            }
        });

        final Button hiButton = findViewById(R.id.chatHello);
        hiButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateTextView("hello!");
            }
        });

        final Button greetingButton = findViewById(R.id.chatGreeting);
        greetingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateTextView("how are you?");
            }
        });

        final Button jokeButton = findViewById(R.id.chatJoke);
        jokeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateTextView("tell me a joke!");
            }
        });
    }

    public void updateTextView(String prompt) {
        TextView textView = findViewById(R.id.LLMResults);
        // create new llminference object, to be able to connect w/ LLM
        LLMInference llm = new LLMInference(this);

        // send prompt + display results
        // FOR FRONTEND: call generateJournalEntry() to generate journal entry and respondToChat() for chatting
        // just add the chat prompt as the last paramater for respondToChat()
        llm.respondToChat("young", "bear", 80, 80, 80, prompt,
                new LLMInference.LLMCallback() {
                    @Override
                    public void onComplete(String llmResult) {
                        // Step 2: Parse the LLM result
                        try {
                            JSONObject json = new JSONObject(llmResult);
                            String mood = json.getString("mood");
                            String summary = json.getString("summary");
                            String entryText = json.getString("summary"); // or get from user input
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        System.out.println("Error generating response");
                    }
                });

        llm.generateJournalEntry(age, petType, 80, 60, 70,
                new LLMInference.LLMCallback() {
                    @Override
                    public void onComplete(String llmResult) {
                        // Step 2: Parse the LLM result
                        try {
                            JSONObject json = new JSONObject(llmResult);
                            String mood = json.getString("mood");
                            String summary = json.getString("summary");
                            String entryText = json.getString("summary"); // or get from user input

                            // Step 3: Send to backend
                            llm.sendToBackend(new Date(), entryText, mood, summary,
                                    new LLMInference.CreateEntryCallback() {
                                        @Override
                                        public void onSuccess(JSONObject response) {
                                            runOnUiThread(() -> {
                                                Toast.makeText(getApplicationContext(),
                                                        "Saved", Toast.LENGTH_SHORT).show();
                                            });
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            runOnUiThread(() -> {
                                                Toast.makeText(getApplicationContext(),
                                                        "Failed to save", Toast.LENGTH_SHORT).show();
                                            });
                                        }
                                    });

                        } catch (JSONException e) {
                            e.printStackTrace();
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        System.out.println("Error generating response");
                    }
                });

    }

}

