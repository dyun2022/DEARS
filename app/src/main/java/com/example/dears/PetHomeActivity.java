package com.example.dears;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class PetHomeActivity extends AppCompatActivity {

    private ImageView ivPetOval;
    private int resolvedUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_home);

        ivPetOval = findViewById(R.id.ivPetOval);
        ImageButton btnSettings = findViewById(R.id.btnSettings);

        int intentUserId = getIntent().getIntExtra("userId", -1);
        if (intentUserId > 0) {
            resolvedUserId = intentUserId;
        } else {
            SharedPreferences sp = getSharedPreferences("auth", MODE_PRIVATE);
            resolvedUserId = sp.getInt("userId", -1);
        }

        String pet = getIntent().getStringExtra("pet");
        if ("Deer".equalsIgnoreCase(pet)) {
            ivPetOval.setImageResource(R.drawable.baby_deer_default);
        } else {
            ivPetOval.setImageResource(R.drawable.baby_bear_default);
        }

        btnSettings.setOnClickListener(v -> {
            Intent i = new Intent(PetHomeActivity.this, SettingsActivity.class);
            i.putExtra("userId", resolvedUserId);
            i.putExtra("username", getIntent().getStringExtra("username"));
            i.putExtra("birthday", getIntent().getStringExtra("birthday"));
            i.putExtra("avatarName", getIntent().getStringExtra("avatarName"));

            startActivity(i);
        });
    }
}
