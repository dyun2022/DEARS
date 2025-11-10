// java
package com.example.dears;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dears.data.api.APIClient;
import com.example.dears.data.api.InterfaceAPI;
import com.example.dears.data.model.Pet;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    private String age;
    private String petType = null;
    private int petID = -1;
    private int userId;
    private Pet pet;
    private int hunger;
    private int energy;
    private int happiness;
    private int growth;
    private int meterMax;
    private String name;
    int timesChatted = 0;

    InterfaceAPI interfaceAPI = APIClient.getClient().create(InterfaceAPI.class);

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

        if (pet == null) {
            Toast.makeText(this, "Missing pet data, returning.", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        // get pet attributes
        name = pet.getName();
        petID = pet.getPetID();
        petType = pet.getType();
        hunger = pet.getHungerMeter();
        energy = pet.getEnergyMeter();
        happiness = pet.getHappinessMeter();
        growth = pet.getGrowthPoints();

        // get age/meters
        age = pet.getAge().getAgeStage();
        meterMax = pet.getAge().getMeterMax();

        setPetImage("default");

        final TextView textView = findViewById(R.id.LLMResults);

        final Button hiButton = findViewById(R.id.chatHello);
        hiButton.setOnClickListener(v -> {
            textView.setText("Loading response from " + name);
            updateTextView(hiButton.getText().toString());
        });

        final Button greetingButton = findViewById(R.id.chatGreeting);
        greetingButton.setOnClickListener(v -> {
            textView.setText("Loading response from " + name);
            updateTextView(greetingButton.getText().toString());
        });

        final Button jokeButton = findViewById(R.id.chatJoke);
        jokeButton.setOnClickListener(v -> {
            textView.setText("Loading response from " + name);
            updateTextView(jokeButton.getText().toString());
        });

        if (savedInstanceState != null) {
            timesChatted = savedInstanceState.getInt("timesChatted", 0);
        }

        final ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("pet", pet);
            resultIntent.putExtra("userId", userId);
            resultIntent.putExtra("updateHappiness", true);
            resultIntent.putExtra("timesChatted", timesChatted);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    public void updateTextView(String prompt) {
        final TextView textView = findViewById(R.id.LLMResults);

        if (pet == null || pet.getAge() == null) {
            Log.e("ChatActivity", "updateTextView: missing pet or age");
            return;
        }

        LLMInference llm;
        try {
            llm = new LLMInference(this);
        } catch (Exception e) {
            Log.e("ChatActivity", "LLM init failed", e);
            Toast.makeText(this, "LLM init error", Toast.LENGTH_SHORT).show();
            return;
        }
        timesChatted += 1;


        // optimistic local increment and UI refresh
        int increment = Math.max(1, Math.round(meterMax * 0.25f));
        happiness = Math.min(meterMax, happiness + increment);
        pet.setHappinessMeter(happiness);
        final View barHappiness = findViewById(R.id.barHappiness);

        if(barHappiness!= null){
            PetUIHelper.updateHappinessBar(barHappiness, pet, this, meterMax);
        } else {
            Log.w("ChatActivity", "barHappiness view is null");
        }

        Call<Pet> chatPet = interfaceAPI.chatPet(userId);
        chatPet.enqueue(new Callback<Pet>() {
            @Override
            public void onResponse(Call<Pet> call, Response<Pet> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pet = response.body();
                    happiness = pet.getHappinessMeter();
                    hunger = pet.getHungerMeter();
                    energy = pet.getEnergyMeter();
                    growth = pet.getGrowthPoints();

                    runOnUiThread(() -> {
                        setPetImage("happy");
                        if (barHappiness != null) {
                            PetUIHelper.updateHappinessBar(barHappiness, pet, ChatActivity.this, meterMax);
                        }
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("pet", pet);
                        resultIntent.putExtra("userId", userId);
                        resultIntent.putExtra("timesChatted", timesChatted);
                        resultIntent.putExtra("updateHappiness", false);
                        setResult(RESULT_OK, resultIntent);
                    });
                } else {
                    Log.w("ChatActivity", "chatPet response not successful");
                }
            }

            @Override
            public void onFailure(Call<Pet> call, Throwable t) {
                Log.e("ChatActivity", "chatPet failed", t);
                fail();
            }
        });

        new Thread(() -> {
            try {
                llm.respondToChat(age, petType, happiness, hunger, energy, prompt, new LLMInference.LLMCallback() {
                    @Override
                    public void onComplete(String llmResult) {
                        runOnUiThread(() -> {
                            try {
                                String processedResult = llmResult.replace("```json", "").replace("```", "").trim();
                                JSONObject json = new JSONObject(processedResult);
                                String response = json.getString("response");
                                textView.setText(response);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Error parsing LLM output", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(() ->
                                Toast.makeText(getApplicationContext(), "Error generating response", Toast.LENGTH_SHORT).show()
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

    private void setPetImage(String action) {
        ImageView petPicture = findViewById(R.id.petPicture);
        if (petPicture == null || pet == null) return;

        Map<String, Integer> petImages = Map.ofEntries(
                Map.entry("adult_bear_default", R.drawable.adult_bear_default),
                Map.entry("adult_bear_happy", R.drawable.adult_bear_happy),
                Map.entry("adult_bear_sleep", R.drawable.adult_bear_sleep),
                Map.entry("teen_bear_default", R.drawable.teen_bear_default),
                Map.entry("teen_bear_happy", R.drawable.teen_bear_happy),
                Map.entry("teen_bear_sleep", R.drawable.teen_bear_sleep),
                Map.entry("baby_bear_default", R.drawable.baby_bear_default),
                Map.entry("baby_bear_happy", R.drawable.baby_bear_happy),
                Map.entry("baby_bear_sleep", R.drawable.baby_bear_sleep),
                Map.entry("adult_deer_default", R.drawable.adult_deer_default),
                Map.entry("adult_deer_happy", R.drawable.adult_deer_happy),
                Map.entry("adult_deer_sleep", R.drawable.adult_deer_sleep),
                Map.entry("teen_deer_default", R.drawable.teen_deer_default),
                Map.entry("teen_deer_happy", R.drawable.teen_deer_happy),
                Map.entry("teen_deer_sleep", R.drawable.teen_deer_sleep),
                Map.entry("baby_deer_default", R.drawable.baby_deer_default),
                Map.entry("baby_deer_happy", R.drawable.baby_deer_happy),
                Map.entry("baby_deer_sleep", R.drawable.baby_deer_sleep)
        );

        String key = pet.getAge().getAgeStage() + "_" + pet.getType().toLowerCase() + "_" + action;
        Integer img = petImages.get(key);
        if (img != null) petPicture.setImageResource(img);
    }

    private void fail() {
        Toast.makeText(ChatActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("timesChatted", timesChatted);
    }
}

