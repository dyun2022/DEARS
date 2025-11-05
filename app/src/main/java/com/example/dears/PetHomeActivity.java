package com.example.dears;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dears.data.api.APIClient;
import com.example.dears.data.api.InterfaceAPI;
import com.example.dears.data.model.AgeStage;
import com.example.dears.data.model.Pet;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetHomeActivity extends AppCompatActivity {
    Pet pet;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_home);
        interfaceAPI = APIClient.getClient().create(InterfaceAPI.class);

        Intent intent = getIntent();
        pet = (Pet) intent.getSerializableExtra("pet");
        userId = intent.getIntExtra("userId", -1);


        // Buttons & Views
        Button btnSleep = findViewById(R.id.btnSleep);
        Button btnFeed = findViewById(R.id.btnFeed);
        Button btnChat = findViewById(R.id.btnChat);
        ImageButton btnJournal = findViewById(R.id.btnJournal);
        ImageButton btnSettings = findViewById(R.id.btnSettings);
        ImageView lowFood = findViewById(R.id.lowFood);
        ImageView midFood = findViewById(R.id.midFood);
        ImageView highFood = findViewById(R.id.highFood);

        // Quick pet view init
        ImageView ivPetOval = findViewById(R.id.ivPetOval);
        ivPetOval.setVisibility(View.VISIBLE);
        ivPetOval.bringToFront();

        setPetImage("default");
        updateEnergyBar();
        updateHungerBar();
        updateHappinessBar();

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
                    updateEnergyBar();
                }
            }

            @Override
            public void onFailure(Call<Pet> call, Throwable t) {
                Toast.makeText(PetHomeActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
            }
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
            public void onFailure(Call<Pet> call, Throwable t) {
                Toast.makeText(PetHomeActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
            }
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
}
