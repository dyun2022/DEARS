package com.example.dears;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dears.data.api.APIClient;
import com.example.dears.data.api.InterfaceAPI;
import com.example.dears.data.model.Journal;
import com.example.dears.data.model.Pet;
import com.example.dears.data.model.User;
import com.example.dears.data.request.createEntryRequest;

import org.json.JSONObject;

import java.time.LocalDate;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JournalActivity extends AppCompatActivity {
    Pet pet;
    int userId;
    int timesChatted;
    int timesFed;
    int timesSleep;
    int day;

    InterfaceAPI interfaceAPI;


    // In order to make demo easier
    final LocalDate defaultDate = LocalDate.of(2025, 11, 5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);
        interfaceAPI = APIClient.getClient().create(InterfaceAPI.class);

        Intent intent = getIntent();
        pet = (Pet) intent.getSerializableExtra("pet");
        userId = intent.getIntExtra("userId", -1);
        timesChatted = intent.getIntExtra("timesChatted", 0);
        timesFed = intent.getIntExtra("timesFed", 0);
        timesSleep = intent.getIntExtra("timesSleep", 0);
        day = intent.getIntExtra("day", 0);

        setPetImage();

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        LinearLayout entries = findViewById(R.id.entriesContainer);

        Call<Journal[]> jReq = interfaceAPI.getAllJournals();

        jReq.enqueue(new Callback<Journal[]>() {
            @Override
            public void onResponse(Call<Journal[]> call, Response<Journal[]> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean needCreateJournal = true;
                    Journal[] journals = response.body();

                    Log.d("JOURNALLOG", Integer.toString(journals.length));

                    for (Journal j : response.body()) {
                        if (j.getPetId() == pet.getPetID()) {
                            entries.addView(createEntryView(j.getSummary()));
                            Log.d("JOURNALLOG", j.getDate().toString() + " vs " + defaultDate.plusDays(day));

                            if (j.getDate().equals(defaultDate.plusDays(day))) needCreateJournal = false;
                        }
                    }

                    if (needCreateJournal) {
                        Call<Journal> cjReq = interfaceAPI.getJournalByPetID(pet.getPetID());

                        cjReq.enqueue(new Callback<Journal>() {
                            @Override
                            public void onResponse(Call<Journal> call, Response<Journal> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    createEntryObj(response.body().getJournalId());
                                } else {
                                    fail();
                                }
                            }

                            @Override
                            public void onFailure(Call<Journal> call, Throwable t) {
                                fail();
                            }
                        });
                    }
                } else { fail(); }
            }

            @Override
            public void onFailure(Call<Journal[]> call, Throwable t) { fail(); }
        });
    }

    private String createEntryObj(int journalId) {
        Log.d("JOURNALLOG", "triggered");

        LLMInference llm = new LLMInference(this);

        // put the llm call on a thread so it doesn't hog all of the resources
        new Thread(() -> {
            try {
                llm.generateJournalEntry(
                        pet.getAge().getAgeStage(),
                        pet.getType(),
                        ( (double) pet.getHappinessMeter()) / pet.getHappiness().getMeterMax(),
                        ( (double) pet.getHungerMeter()) / pet.getHunger().getMeterMax(),
                        ( (double) pet.getEnergyMeter()) / pet.getEnergy().getMeterMax(),
                        new LLMInference.LLMCallback() {
                    @Override
                    public void onComplete(String llmResult) {
                        System.out.println(llmResult);
                        runOnUiThread(() -> {
                            try {
                                Log.d("JOURNALLOG", Double.toString(( (double) pet.getHappinessMeter()) / pet.getHappiness().getMeterMax()));
                                String processedResult = llmResult.replace("```json", "").replace("```", "").trim();
                                Log.d("JOURNALLOG", "RAW LLM OUTPUT:\n" + llmResult);
                                JSONObject json = new JSONObject(processedResult);
                                String response = "Day " + day + "\n";
                                String summary = json.getString("summary");
                                int mood = json.getInt("mood");
                                response += summary;
                                if (timesChatted != 0) response += " You chatted with me " + timesChatted + " time" + ((timesChatted == 1) ? "!" : "s!");
                                if (timesFed != 0) response += " You fed me " + timesFed + " time" + ((timesChatted == 1) ? "!" : "s!") ;
                                if (timesSleep != 0) response += " I napped " + timesSleep + " time" + ((timesChatted == 1) ? "!" : "s!") ;

                                Log.d("JOURNALLOG", response);
                                LinearLayout entries = findViewById(R.id.entriesContainer);
                                entries.addView(createEntryView(response));

                                createEntryRequest creReq = new createEntryRequest(Integer.toString(mood), response, Integer.toString(pet.getPetID()), Integer.toString(journalId));
                                Call<Object> creRes = interfaceAPI.createEntry(defaultDate.plusDays(1).toString(), creReq);
                                creRes.enqueue(new Callback<Object>() {
                                    @Override
                                    public void onResponse(Call<Object> call, Response<Object> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                        } else {
                                            fail();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Object> call, Throwable t) {
                                        fail();
                                    }
                                });
                            } catch (Exception e) {
                                Log.d("JOURNALLOG", "exception");
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
        return "";
    }

    private void setPetImage() {
        ImageView petImage = findViewById(R.id.petImage);

        // Dictionary to make grabbing the image easier
        Map<String, Integer> petImages = Map.ofEntries(
                Map.entry("adult_bear_default", R.drawable.adult_bear_default),
                Map.entry("teen_bear_default", R.drawable.teen_bear_default),
                Map.entry("baby_bear_default", R.drawable.baby_bear_default),
                Map.entry("adult_deer_default", R.drawable.adult_deer_default),
                Map.entry("teen_deer_default", R.drawable.teen_deer_default),
                Map.entry("baby_deer_default", R.drawable.baby_deer_default)
        );

        String key = "";
        key += pet.getAge().getAgeStage() + "_";
        key += pet.getType().toLowerCase() + "_";
        key += "default";

        Integer img = petImages.get(key);
        if (img != null) petImage.setImageResource(img);
    }

    private TextView createEntryView (String entry) {
        TextView entryView = new TextView(this);
        entryView.setText(entry);
        entryView.setTextSize(16);
        entryView.setTextColor(Color.parseColor("#3F5743"));
        entryView.setBackgroundResource(R.drawable.journal_entry); // your rounded bg drawable
        entryView.setPadding(40, 30, 40, 30);

        // Set layout params to give spacing between entries
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 24); // bottom spacing between entries
        entryView.setLayoutParams(params);
        return entryView;
    }

    private void fail() {
        Toast.makeText(JournalActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
    }
}
