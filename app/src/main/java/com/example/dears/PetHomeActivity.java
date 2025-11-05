package com.example.dears;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.dears.data.api.APIClient;
import com.example.dears.data.api.InterfaceAPI;
import com.example.dears.data.model.MainViewModel;
import com.example.dears.data.model.Pet;
import com.example.dears.data.model.User;
import com.example.dears.data.request.updatePetRequest;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetHomeActivity extends AppCompatActivity {
    // private MainViewModel mainViewModel;

    Pet pet;
    int ageId;
    int userId;
    int clock;
    // The width of the status bars *IN DP*
    final int barWidth = 120;
    int timesChatted = 0;
    int timesFed = 0;
    int timesSleep = 0;
    boolean isSleeping = false;
    boolean isHappy = false;
    InterfaceAPI interfaceAPI;

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save pet object
        savedInstanceState.putSerializable("pet", pet);
        savedInstanceState.putInt("userId", userId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_home);
        interfaceAPI = APIClient.getClient().create(InterfaceAPI.class);
        // Persist data
        // mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        Intent intent = getIntent();
        pet = (Pet) intent.getSerializableExtra("pet");
        userId = intent.getIntExtra("userId", -1);

        // Smarter way to persist data; right now, going to rely on intents
        /*if (pet != null) { mann idk
            mainViewModel.setPet(pet);
            mainViewModel.setUserId(userId);
        }
        else {
            mainViewModel.getPet().observe(this, p -> {
                if (p != null) {
                    pet = p;
                    updateBars();
                }
            });

            mainViewModel.getUserId().observe(this, id -> userId = id);
        }*/

        ageId = pet.getAge().getAgeID();

        // Buttons & Views
        Button btnSleep = findViewById(R.id.btnSleep);
        Button btnFeed = findViewById(R.id.btnFeed);
        Button btnChat = findViewById(R.id.btnChat);
        ImageButton btnJournal = findViewById(R.id.btnJournal);
        ImageButton btnSettings = findViewById(R.id.btnSettings);
        ImageView lowFood = findViewById(R.id.lowFood);
        ImageView midFood = findViewById(R.id.midFood);
        ImageView highFood = findViewById(R.id.highFood);

        btnChat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(PetHomeActivity.this, ChatActivity.class);
                intent.putExtra("pet", pet);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

        btnSettings.setOnClickListener(v -> {
            Call<User> getUser = interfaceAPI.getUserById(userId);

            getUser.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Intent i = new Intent(PetHomeActivity.this, SettingsActivity.class);
                        i.putExtra("userId", userId);
                        i.putExtra("pet", pet);
                        i.putExtra("username", response.body().getUsername());
                        i.putExtra("birthday", response.body().getBirthday());
                        i.putExtra("avatarName", response.body().getAvatar());
                        startActivity(i);
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) { fail(); }
            });
        });

        // Quick pet view init
        ImageView ivPetOval = findViewById(R.id.ivPetOval);
        ivPetOval.setVisibility(View.VISIBLE);
        ivPetOval.bringToFront();

        setPetImage("default");
        updateBars();

        // Initialize foods.
        if (pet.getType().equals("Deer")) {
            lowFood.setImageResource(R.drawable.bark);
            midFood.setImageResource(R.drawable.berries);
            highFood.setImageResource(R.drawable.mushroom);
        } else {
            lowFood.setImageResource(R.drawable.honey);
            midFood.setImageResource(R.drawable.berries);
            highFood.setImageResource(R.drawable.salmon);
        }

        // Sleep logic
        btnSleep.setOnClickListener(v -> {
            // * TO-DO * Make pet not sleep if happiness is at max
            if (!isSleeping) {
                btnFeed.setVisibility(View.GONE);
                btnChat.setVisibility(View.GONE);
                btnJournal.setVisibility(View.GONE);
                btnSettings.setVisibility(View.GONE);
                btnSleep.setText("It's time to wake up!");
                setPetImage("sleep");
                isSleeping = true;
            } else {
                btnFeed.setVisibility(View.VISIBLE);
                btnChat.setVisibility(View.VISIBLE);
                btnJournal.setVisibility(View.VISIBLE);
                btnSettings.setVisibility(View.VISIBLE);
                btnSleep.setText("Sleepy time!");
                setPetImage("default");
                isSleeping = false;
            }
        });

        // Food logic
        btnFeed.setOnClickListener( v -> {
            btnFeed.setVisibility(View.GONE);
            btnChat.setVisibility(View.GONE);
            btnSleep.setVisibility(View.GONE);
            lowFood.setVisibility(View.VISIBLE);
            midFood.setVisibility(View.VISIBLE);
            highFood.setVisibility(View.VISIBLE);
        });

        lowFood.setOnClickListener(v -> {
            String food = pet.getType().equals("Deer") ? "bark" : "honey";
            petFeed(food);
        });

        midFood.setOnClickListener(v -> {
            petFeed("berries");
        });

        highFood.setOnClickListener(v -> {
            String food = pet.getType().equals("Deer") ? "mushroom" : "salmon";
            petFeed(food);
        });

        runClock();
    }

    private void runClock() {
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                clock++;
                if (isSleeping) petSleep();
                // TO-DO: Implement non-buggy status decay while sleeping
                //eelse statusDecay();
                handler.postDelayed(this, 1000);
            }
        });
    }

    // Action: happy, sleep, or default
    private void setPetImage(String action) {
        ImageView ivPetOval = findViewById(R.id.ivPetOval);

        // Dictionary to make grabbing the image easier
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

        String key = "";
        key += pet.getAge().getAgeStage() + "_";
        key += pet.getType().toLowerCase() + "_";
        key += action;

        Integer img = petImages.get(key);
        if (img != null) ivPetOval.setImageResource(img);

        int newAgeId = pet.getAge().getAgeID();
        // Pet grew up!
        if (ageId != newAgeId) {
            isSleeping = false;
            ageId = newAgeId;
            happyReaction();
            Toast.makeText(PetHomeActivity.this, "Your pet grew!", Toast.LENGTH_SHORT).show();
        }
    }

    private void happyReaction() {
        setPetImage("happy");
        new Handler().postDelayed(() -> {
            setPetImage("default");
        }, 500);
    }
    private void petSleep() {
        Call<Pet> petSleep = interfaceAPI.sleepPet(pet.getPetID());

        petSleep.enqueue(new Callback<Pet>() {
            @Override
            public void onResponse(Call<Pet> call, Response<Pet> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pet = response.body();
                    // mainViewModel.setPet(pet);
                    updateEnergyBar();
                }
            }

            @Override
            public void onFailure(Call<Pet> call, Throwable t) { fail(); }
        });
    }

    private void petFeed(String food) {
        // This is admittedly bad coding practice
        // Might make more robust in a later version
        Map<String, Integer> foodToId = Map.of(
                "bark", 1,
                "berries", 2,
                "mushroom", 3,
                "honey", 4,
                "salmon", 5
        );
        ImageView lowFood = findViewById(R.id.lowFood);
        ImageView midFood = findViewById(R.id.midFood);
        ImageView highFood = findViewById(R.id.highFood);
        Button btnSleep = findViewById(R.id.btnSleep);
        Button btnFeed = findViewById(R.id.btnFeed);
        Button btnChat = findViewById(R.id.btnChat);

        Call<Pet> petFeed = interfaceAPI.feedPet(pet.getPetID(), foodToId.get(food));

        petFeed.enqueue(new Callback<Pet>() {
            @Override
            public void onResponse(Call<Pet> call, Response<Pet> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pet = response.body();
                    // mainViewModel.setPet(pet);
                    setPetImage("default");
                    updateHungerBar();
                    btnFeed.setVisibility(View.VISIBLE);
                    btnChat.setVisibility(View.VISIBLE);
                    btnSleep.setVisibility(View.VISIBLE);
                    lowFood.setVisibility(View.GONE);
                    midFood.setVisibility(View.GONE);
                    highFood.setVisibility(View.GONE);
                    happyReaction();
                }
            }

            @Override
            public void onFailure(Call<Pet> call, Throwable t) { fail(); }
        });
    }
    private void updateEnergyBar() {
        View barEnergy = findViewById(R.id.barEnergy);
        int barMax = (int) (barWidth * getResources().getDisplayMetrics().density);
        double barPercent = ((double) pet.getEnergyMeter()) / pet.getEnergy().getMeterMax();
        int updatedWidth = (int) (barMax * barPercent);

        ViewGroup.LayoutParams params = barEnergy.getLayoutParams();
        params.width = updatedWidth;
        barEnergy.setLayoutParams(params);
    }

    private void updateHungerBar() {
        View barHunger = findViewById(R.id.barHunger);
        int barMax = (int) (barWidth * getResources().getDisplayMetrics().density);
        double barPercent = ((double) pet.getHungerMeter()) / pet.getHunger().getMeterMax();
        int updatedWidth = (int) (barMax * barPercent);

        ViewGroup.LayoutParams params = barHunger.getLayoutParams();
        params.width = updatedWidth;
        barHunger.setLayoutParams(params);
    }

    private void updateHappinessBar() {
        View barHappiness = findViewById(R.id.barHappiness);
        int barMax = (int) (barWidth * getResources().getDisplayMetrics().density);
        double barPercent = ((double) pet.getHappinessMeter()) / pet.getHappiness().getMeterMax();
        int updatedWidth = (int) (barMax * barPercent);

        ViewGroup.LayoutParams params = barHappiness.getLayoutParams();
        params.width = updatedWidth;
        barHappiness.setLayoutParams(params);
    }

    private void statusDecay() {
        int decay = 1;
        int energyDecay = Math.max(pet.getEnergyMeter() - decay, 0);
        int hungerDecay = Math.max(pet.getHungerMeter() - decay, 0);
        int happinessDecay = Math.max(pet.getHappinessMeter() - decay, 0);
        updatePetRequest upr = new updatePetRequest(hungerDecay, happinessDecay, energyDecay);
        Call<Pet> updatePet = interfaceAPI.updatePet(pet.getPetID(), upr);

        updatePet.enqueue(new Callback<Pet>() {
            @Override
            public void onResponse(Call<Pet> call, Response<Pet> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pet = response.body();
                    // mainViewModel.setPet(pet);
                    setPetImage("default");
                    updateBars();
                }
            }

            @Override
            public void onFailure(Call<Pet> call, Throwable t) { fail(); }
        });
    }

    private void fail() {
        Toast.makeText(PetHomeActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
    }

    private void updateBars() {
        updateEnergyBar();
        updateHappinessBar();
        updateHungerBar();
    }
}
