package com.example.dears;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class PetHomeActivity extends AppCompatActivity {

    private ImageView ivPetOval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_home);

        ivPetOval = findViewById(R.id.ivPetOval);
        ImageButton btnSettings = findViewById(R.id.btnSettings);

        // Always read the id that Register/Login placed here:
        final int currentUserId = getIntent().getIntExtra("userId", -1);
        String pet = getIntent().getStringExtra("pet"); // "Deer" or "Bear"

        // Show correct pet chosen at registration
        if ("Deer".equalsIgnoreCase(pet)) {
            ivPetOval.setImageResource(R.drawable.baby_deer_default);
        } else {
            ivPetOval.setImageResource(R.drawable.baby_bear_default);
        }

        btnSettings.setOnClickListener(v -> {
            Intent i = new Intent(PetHomeActivity.this, SettingsActivity.class);
            // CRITICAL: forward the same id so Settings updates the right user
            i.putExtra("userId", currentUserId);
            i.putExtra("username", getIntent().getStringExtra("username"));
            i.putExtra("birthday", getIntent().getStringExtra("birthday"));
            i.putExtra("avatarName", getIntent().getStringExtra("avatarName"));
            startActivity(i);
        });
    }
}
