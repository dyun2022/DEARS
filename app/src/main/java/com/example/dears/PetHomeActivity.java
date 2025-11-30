package com.example.dears;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dears.data.api.APIClient;
import com.example.dears.data.api.InterfaceAPI;
import com.example.dears.data.model.Pet;
import com.example.dears.data.model.User;
import com.example.dears.data.request.updatePetRequest;

import java.util.HashMap;
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
    static public final int barWidth = 120;
    int timesChatted = 0;
    int timesFed = 0;
    int timesSleep = 0;
    int day = 1;
    boolean isSleeping = false;
    boolean isHappy = false;
    private boolean isPlayingAnimation = false;

    InterfaceAPI interfaceAPI;
    private Handler handler = new Handler();
    private Runnable clockRunnable;
    private boolean clockRunning;
    private static final int REQUEST_CHAT = 1001;
    private User user;

    private ActivityResultLauncher<Intent> chatLauncher;

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

        chatLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                (ActivityResult result) -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        Pet updated = (Pet) data.getSerializableExtra("pet");
                        if (data != null && data.hasExtra("pet")) {
                            pet = updated;
                            runOnUiThread(() -> {
                                updateBars();
                                setPetImage("default");
                            });
                        }
                    }
                }
        );

        Intent intent = getIntent();
        pet = (Pet) intent.getSerializableExtra("pet");
        user = (User) intent.getSerializableExtra("user");
        userId = intent.getIntExtra("userId", -1);
        if (intent.getBooleanExtra("updateHappiness", false)) {
            updateHappinessBar();
        }

        ageId = pet.getAge().getAgeID();

        // Buttons & Views
        Button btnSleep = findViewById(R.id.btnSleep);
        Button btnFeed = findViewById(R.id.btnFeed);
        Button btnChat = findViewById(R.id.btnChat);
        Button btnPlay = findViewById(R.id.btnPlay);
        ImageButton btnJournal = findViewById(R.id.btnJournal);
        ImageButton btnSettings = findViewById(R.id.btnSettings);



        ImageView lowFood = findViewById(R.id.lowFood);
        ImageView midFood = findViewById(R.id.midFood);
        ImageView highFood = findViewById(R.id.highFood);

        btnChat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent chatIntent = new Intent(PetHomeActivity.this, ChatActivity.class);
                chatIntent.putExtra("pet", pet);
                chatIntent.putExtra("userId", userId);
                chatLauncher.launch(chatIntent);
            }
        });

        btnSettings.setOnClickListener(v -> {
            Call<User> getUser = interfaceAPI.getUserById(userId);

            getUser.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body();
                        ImageButton btnSettings = findViewById(R.id.btnSettings);
                        setAvatarImage(btnSettings, user.getAvatar());

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
                public void onFailure(Call<User> call, Throwable t) {
                    fail();
                }
            });
        });

        btnJournal.setOnClickListener(v -> {
            Intent i = new Intent(PetHomeActivity.this, JournalActivity.class);
            i.putExtra("userId", userId);
            i.putExtra("pet", pet);
            i.putExtra("timesSleep", timesSleep);
            i.putExtra("timesFed", timesFed);
            i.putExtra("timesChatted", timesChatted);
            i.putExtra("day", day);
            startActivity(i);
        });

        // Quick pet view init
        ImageView ivPetOval = findViewById(R.id.ivPetOval);
        ivPetOval.setVisibility(View.VISIBLE);
        ivPetOval.bringToFront();

        setPetImage("default");
//        interfaceAPI.getUserById(userId).enqueue(new Callback<User>() {
//            @Override public void onResponse(Call<User> call, Response<User> resp) {
//                if (!resp.isSuccessful() || resp.body() == null) return;
//                user = resp.body();
//
//            }
//            @Override public void onFailure(Call<User> call, Throwable t) {}
//        });



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
            if (pet.getEnergyMeter() >= pet.getEnergy().getMeterMax()) {
                showToast("Pet is too energized to sleep!");
                return;
            }

            timesSleep += 1;
            if (!isSleeping) {
                btnFeed.setVisibility(View.GONE);
                btnChat.setVisibility(View.GONE);
                btnJournal.setVisibility(View.GONE);
                btnSettings.setVisibility(View.GONE);
                btnPlay.setVisibility(View.GONE);
                btnSleep.setText("It's time to wake up!");
                setPetImage("sleep");
                isSleeping = true;
            } else {
                wakeUp();
            }
        });

        // Food logic
        btnFeed.setOnClickListener(v -> {
            if (pet.getHungerMeter() >= pet.getHunger().getMeterMax()) {
                showToast("Pet is too full to eat!");
                return;
            }
            timesFed += 1;
            btnFeed.setVisibility(View.GONE);
            btnChat.setVisibility(View.GONE);
            btnSleep.setVisibility(View.GONE);
            btnPlay.setVisibility(View.GONE);
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

        btnPlay.setOnClickListener(v -> {
            if (isPlayingAnimation) return;

            playAnimation();

            int curHappy = pet.getHappinessMeter();
            int curHunger = pet.getHungerMeter();
            int curEnergy = pet.getEnergyMeter();

            int maxHappy = pet.getHappiness().getMeterMax();
            int maxHunger = pet.getHunger().getMeterMax();
            int maxEnergy = pet.getEnergy().getMeterMax();

            int newHappy = Math.min(curHappy + 5, maxHappy);
            int newHunger = Math.max(curHunger - 5, 0);
            int newEnergy = Math.max(curEnergy - 5, 0);

            updatePetRequest upr = new updatePetRequest(newHunger, newHappy, newEnergy);

            Call<Pet> updatePet = interfaceAPI.updatePet(pet.getPetID(), upr);
            updatePet.enqueue(new Callback<Pet>() {
                @Override
                public void onResponse(Call<Pet> call, Response<Pet> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        pet = response.body();
                        updateBars();
                    }
                }

                @Override
                public void onFailure(Call<Pet> call, Throwable t) {
                    fail();
                }
            });
        });

        runClock();
    }
    private void playAnimation() {
        ImageView ivPetOval = findViewById(R.id.ivPetOval);

        int[] frames = {
                R.drawable.play_placeholder1,
                R.drawable.play_placeholder2,
                R.drawable.play_placeholder3
        };

        int delay = 1000; // 1 second between frames

        isPlayingAnimation = true;

        Handler animationHandler = new Handler();
        for (int i = 0; i < frames.length; i++) {
            int frameIndex = i;
            animationHandler.postDelayed(() -> {
                ivPetOval.setImageResource(frames[frameIndex]);
            }, frameIndex * delay);
        }

        animationHandler.postDelayed(() -> {
            isPlayingAnimation = false;
            if (!isSleeping) {
                setPetImage("default");
            }
        }, frames.length * delay);
    }

    private void runClock() {
        if (clockRunning) return;

        clockRunning = true;
        clockRunnable = new Runnable() {
            @Override
            public void run() {
                // TO-DO: The clock still runs when navigating to other pages...
                clock++;
                if (isSleeping) {
                    if (pet.getEnergyMeter() == pet.getEnergy().getMeterMax()) wakeUp();
                    else petSleep();
                }
                // TO-DO: Implement non-buggy status decay while sleeping
                else if (clock % 3 == 0) statusDecay();

                // New "day" every 5 minutes
                if (clock % 300 == 0) {
                    clock = 0;
                    timesChatted = 0;
                    timesFed = 0;
                    timesSleep = 0;
                    day += 1;
                }
                handler.postDelayed(this, 1000);
            }
        };

        handler.post(clockRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(clockRunnable);
        SharedPreferences prefs = getSharedPreferences("PetPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("clock", clock);
        editor.putInt("day", day);
        editor.putInt("timesFed", timesFed);
        editor.putInt("timesChatted", timesChatted);
        editor.putInt("timesSleep", timesSleep);
        editor.apply();
        clockRunning = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(clockRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("PetPrefs", MODE_PRIVATE);
        clock = prefs.getInt("clock", 0);
        day = prefs.getInt("day", 1);
        timesFed = prefs.getInt("timesFed", 0);
        timesChatted = prefs.getInt("timesChatted", 0);
        timesSleep = prefs.getInt("timesSleep", 0);

        runClock(); // restart clock when resuming
    }

    public void wakeUp() {
        Button btnSleep = findViewById(R.id.btnSleep);
        Button btnFeed = findViewById(R.id.btnFeed);
        Button btnChat = findViewById(R.id.btnChat);
        Button btnPlay = findViewById(R.id.btnPlay);
        ImageButton btnJournal = findViewById(R.id.btnJournal);
        ImageButton btnSettings = findViewById(R.id.btnSettings);

        btnFeed.setVisibility(View.VISIBLE);
        btnChat.setVisibility(View.VISIBLE);
        btnJournal.setVisibility(View.VISIBLE);
        btnSettings.setVisibility(View.VISIBLE);
        btnPlay.setVisibility(View.VISIBLE);
        btnSleep.setText("Sleepy time!");
        setPetImage("default");
        isSleeping = false;
    }

    // Action: happy, sleep, or default
    public void setPetImage(String action) {
        if (isPlayingAnimation) {
            return;
        }

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

    public void happyReaction() {
        setPetImage("happy");
        new Handler().postDelayed(() -> setPetImage("default"), 500);
    }

    public void petSleep() {
        if (isSleeping && pet.getEnergyMeter() >= pet.getEnergy().getMeterMax()) return;

        Call<Pet> petSleep = interfaceAPI.sleepPet(pet.getPetID());

        petSleep.enqueue(new Callback<Pet>() {
            @Override
            public void onResponse(Call<Pet> call, Response<Pet> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pet = response.body();
                    updateEnergyBar();

                    if (pet.getEnergyMeter() >= pet.getEnergy().getMeterMax()) {
                        wakeUp();
                    }
                }
            }

            @Override
            public void onFailure(Call<Pet> call, Throwable t) {
                fail();
            }
        });
    }

    public void petFeed(String food) {
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
        Button btnPlay = findViewById(R.id.btnPlay);

        Call<Pet> petFeed = interfaceAPI.feedPet(pet.getPetID(), foodToId.get(food));

        petFeed.enqueue(new Callback<Pet>() {
            @Override
            public void onResponse(Call<Pet> call, Response<Pet> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pet = response.body();
                    setPetImage("default");
                    updateHungerBar();
                    btnFeed.setVisibility(View.VISIBLE);
                    btnChat.setVisibility(View.VISIBLE);
                    btnSleep.setVisibility(View.VISIBLE);
                    btnPlay.setVisibility(View.VISIBLE);
                    lowFood.setVisibility(View.GONE);
                    midFood.setVisibility(View.GONE);
                    highFood.setVisibility(View.GONE);
                    happyReaction();
                }
            }

            @Override
            public void onFailure(Call<Pet> call, Throwable t) {
                fail();
            }
        });
    }

    public void updateEnergyBar() {
        TextView energyTextView = findViewById(R.id.tvEnergy);
        energyTextView.setText("energy: " + pet.getEnergyMeter() + " / " + pet.getEnergy().getMeterMax());
        View barEnergy = findViewById(R.id.barEnergy);
        int barMax = (int) (barWidth * getResources().getDisplayMetrics().density);

        ViewGroup.LayoutParams params = barEnergy.getLayoutParams();
        params.width = getUpdatedWidth(pet.getEnergyMeter(), pet.getEnergy().getMeterMax(), barMax);
        barEnergy.setLayoutParams(params);
    }

    public void updateHungerBar() {
        TextView hungerTextView = findViewById(R.id.tvHunger);
        hungerTextView.setText("hunger: " + pet.getHungerMeter() + " / " + pet.getHunger().getMeterMax());
        View barHunger = findViewById(R.id.barHunger);
        int barMax = (int) (barWidth * getResources().getDisplayMetrics().density);

        ViewGroup.LayoutParams params = barHunger.getLayoutParams();
        params.width = getUpdatedWidth(pet.getHungerMeter(), pet.getHunger().getMeterMax(), barMax);
        barHunger.setLayoutParams(params);
    }

    private void updateHappinessBar() {
        View barHappiness = findViewById(R.id.barHappiness);
        if (barHappiness == null || pet == null || pet.getAge() == null) {
            return;
        }
        TextView happyTextView = findViewById(R.id.tvHappiness);
        happyTextView.setText("happiness: " + pet.getHappinessMeter() + " / " + pet.getHappiness().getMeterMax());
        int meterMax = pet.getAge().getMeterMax();
        PetUIHelper.updateHappinessBar(barHappiness, pet, this, meterMax);
    }

    public int getUpdatedWidth(int value, int max, int width) {
        double percent = ((double) value) / max;
        return (int) (width * percent);
    }

    public void statusDecay() {
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
                    setPetImage("default");
                    updateBars();
                }
            }

            @Override
            public void onFailure(Call<Pet> call, Throwable t) {
                fail();
            }
        });
    }

    public void fail() {
        showToast("Something went wrong, please try again");
    }

    public void showToast(String message) {
        Toast.makeText(PetHomeActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public void updateBars() {
        updateEnergyBar();
        updateHappinessBar();
        updateHungerBar();
        updateExpBar();
    }

    public void updateExpBar() {
        TextView expTextView = findViewById(R.id.tvExp);
        expTextView.setText("exp: " + pet.getGrowthPoints() + " / " + pet.getAge());

        View barExp = findViewById(R.id.barExp);
        int barMax = (int) (barWidth * getResources().getDisplayMetrics().density);

        ViewGroup.LayoutParams params = barExp.getLayoutParams();
        params.width = getUpdatedWidth(pet.getGrowthPoints(), pet.getAge().getMeterMax(), barMax);
        barExp.setLayoutParams(params);
    }

    private void setAvatarImage(ImageButton button, String avatarName) {
        if (avatarName == null) return;

        // looks for drawable with the SAME NAME as the avatar
        int imageResId = getResources().getIdentifier(avatarName.toLowerCase(), "drawable", getPackageName());

        if (imageResId != 0) {
            button.setImageResource(imageResId);
        } else {
            // fallback image if name doesn't match drawable
            button.setImageResource(R.drawable.mushroom);
        }
    }

}
