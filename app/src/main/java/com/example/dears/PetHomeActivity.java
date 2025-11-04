package com.example.dears;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class PetHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_home);

        ImageView ivPetOval = findViewById(R.id.ivPetOval);
        String pet = getIntent().getStringExtra("pet");

        if ("Deer".equalsIgnoreCase(pet)) {
            ivPetOval.setImageResource(R.drawable.baby_deer_default);
        } else {
            ivPetOval.setImageResource(R.drawable.baby_bear_default);
        }
    }
}
