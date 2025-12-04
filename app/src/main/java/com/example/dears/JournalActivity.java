package com.example.dears;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dears.data.api.APIClient;
import com.example.dears.data.api.InterfaceAPI;
import com.example.dears.data.model.AgeStage;
import com.example.dears.data.model.Energy;
import com.example.dears.data.model.Happiness;
import com.example.dears.data.model.Hunger;
import com.example.dears.data.model.Journal;
import com.example.dears.data.model.Pet;
import com.example.dears.data.model.User;
import com.example.dears.data.request.createEntryRequest;

import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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
    String day;

    JournalLogic jl;
    LLMInference llm;
    InterfaceAPI interfaceAPI;
    private boolean isCreatingEntry = false;
    Call<Journal[]> jReq;
    LinearLayout entries;
    boolean needCreateJournal = true;
    TextView todayEntry;

    // In order to make demo easier
    final LocalDate defaultDate = LocalDate.of(2025, 11, 5);
    private LocalDate today = LocalDate.now();
    DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);
        interfaceAPI = APIClient.getClient().create(InterfaceAPI.class);

        Log.e("JournalActivity", "About to create LLMInference");
        llm = new LLMInference(this);
        Log.e("JournalActivity", "LLMInference created successfully");

        Intent intent = getIntent();
        pet = (Pet) intent.getSerializableExtra("pet");
        userId = intent.getIntExtra("userId", -1);
        timesChatted = intent.getIntExtra("timesChatted", 0);
        timesFed = intent.getIntExtra("timesFed", 0);
        timesSleep = intent.getIntExtra("timesSleep", 0);
        day = dateformatter.format(today);

        // for testing
        if (pet == null) {
            pet = new Pet(1, "dears", "deer", new AgeStage(1, "baby", 20), 10, new Hunger(), 10, new Happiness(), 10, new Energy(), 10);
        }

        jl = new JournalLogic(pet, userId, timesChatted, timesFed, timesSleep, day);
        setPetImage();

        // back button
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        entries = findViewById(R.id.entriesContainer);
        jReq = interfaceAPI.getAllJournals();
        jReq.enqueue(new Callback<Journal[]>() {
            @Override
            public void onResponse(Call<Journal[]> call, Response<Journal[]> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Journal[] journals = response.body();

                    // get journal entries that correspond to this pet
                    for (Journal j : response.body()) {
                        if (j.getPetId() == pet.getPetID()) {
                            TextView tv = createEntryView(j.getSummary());
                            entries.addView(tv);

                            // entry to be updated later potentially
                            String entryDate = String.valueOf(j.getDate());
                            Log.d("JOURNAL", "entry date: " + entryDate);
                            Log.d("JOURNAL", "day: " + day);
                            if (entryDate.equals(day)) {
                                todayEntry = tv;
                                Log.d("JOURNAL", "Entry already exists for today 1");
                                Log.d("JOURNAL", todayEntry.getText().toString());
                                needCreateJournal = false;
                            }

                            if (entryDate != null && entryDate.trim().equals(day.trim())) {
                                Log.d("JOURNAL", "Entry already exists for today 2");
                                needCreateJournal = false;
                            }
                        }
                    }
                } else { fail(); }
            }

            @Override
            public void onFailure(Call<Journal[]> call, Throwable t) { fail(); }
        });

        // generate new entry button
//        final Button newEntryBtn = findViewById(R.id.generateEntryBtn);
//        newEntryBtn.setOnClickListener(v -> {
//            newEntryBtn.setText("Writing...");
//            createEntryObj(pet.getPetID());
//        });
        createEntryObj(pet.getPetID());
    }

    public void genNewEntry() {
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
    }

    public void createEntryObj(int journalId) {
        Log.d("JOURNALLOG", "createEntryObj triggered");

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
                            // System.out.println(llmResult);
                            Log.d("JOURNALLOG", "RAW LLM OUTPUT:\n" + llmResult);
                            runOnUiThread(() -> {
                                try {
                                    // parse through generated result
                                    Log.d("JOURNALLOG", Double.toString(( (double) pet.getHappinessMeter()) / pet.getHappiness().getMeterMax()));
                                    String processedResult = llmResult.replace("```json", "").replace("```", "").trim();
                                    processedResult = processedResult.replace("“", "\"").replace("”", "\"").replace("-", " ");
                                    JSONObject json = new JSONObject(processedResult);

                                    String summary = json.getString("summary");
                                    int mood = json.getInt("mood");
                                    String response = jl.writeEntry(day, summary, timesChatted, timesFed, timesSleep);
                                    Log.d("JOURNALLOG", "line 187: "+ response);
                                    LinearLayout entries = findViewById(R.id.entriesContainer);

                                    // check if entry exists
                                    LocalDate today = LocalDate.parse(day, dateformatter);
                                    Call<Object> checkCall = interfaceAPI.getEntryByDate(today, pet.getPetID(), journalId);
                                    Log.d("JOURNAL", "line 192 " + checkCall.toString());

                                    checkCall.enqueue(new Callback<Object>() {
                                        @Override
                                        public void onResponse(Call<Object> call, Response<Object> checkRes) {
                                            Log.d("JOURNALLOG", "Checking if entry exists " + call + checkRes);

                                            // if entry is already created, update existing entry
                                            if (checkRes.code() == 200) {
                                                Log.d("JOURNALLOG", "Entry exists → Updating");

                                                Map<String, String> updateBody = new HashMap<>();
                                                updateBody.put("pet_id", Integer.toString(pet.getPetID()));
                                                updateBody.put("journal_id", Integer.toString(journalId));
                                                updateBody.put("mood", Integer.toString(mood));
                                                updateBody.put("summary", response);

                                                Call<Pet> patchCall = interfaceAPI.updateEntry(day.toString(), updateBody);

                                                patchCall.enqueue(new Callback<Pet>() {

                                                    @Override
                                                    public void onResponse(Call<Pet> call, Response<Pet> patchRes) {
                                                        Log.d("JOURNALLOG", "Updated Existing Entry");
                                                    }

                                                    @Override
                                                    public void onFailure(Call<Pet> call, Throwable t) {
                                                        fail();
                                                    }
                                                });
                                                Log.d("JOURNALLOG", "response: " + response);
                                                todayEntry.setText(response);
                                            }

                                            // entry doesn't exist yet, create new entry
                                            else if (checkRes.code() == 404) {
                                                Log.d("JOURNALLOG", "No entry found → Creating new entry");

                                                createEntryRequest creReq = new createEntryRequest(
                                                        Integer.toString(mood),
                                                        response,
                                                        Integer.toString(pet.getPetID()),
                                                        Integer.toString(journalId)
                                                );

                                                Call<Object> creRes = interfaceAPI.createEntry(day, creReq);
                                                creRes.enqueue(new Callback<Object>() {
                                                    @Override
                                                    public void onResponse(Call<Object> call, Response<Object> response) {
                                                        if (!response.isSuccessful()) fail();
                                                    }

                                                    @Override
                                                    public void onFailure(Call<Object> call, Throwable t) {
                                                        fail();
                                                    }
                                                });
                                                entries.addView(createEntryView(response));
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Object> call, Throwable t) {
                                            Log.d("JOURNALLOG", "Error checking entry");
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

        final Button newEntryBtn = findViewById(R.id.generateEntryBtn);
        newEntryBtn.setText("Write Entry");
    }

    void setPetImage() {
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

        String key = jl.getPetImageKey();

        Integer img = petImages.get(key);
        if (img != null) petImage.setImageResource(img);
    }

    public TextView createEntryView (String entry) {
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

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public void setLLMInference(LLMInference llm) {
        this.llm = llm;
    }

    public void setInterfaceAPI(InterfaceAPI api) {
        this.interfaceAPI = api;
    }
}
