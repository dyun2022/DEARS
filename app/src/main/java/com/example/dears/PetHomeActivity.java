package com.example.dears;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dears.data.model.Pet;

public class PetHomeActivity extends AppCompatActivity {
    Pet pet;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_home);

        ImageView ivPetOval = findViewById(R.id.ivPetOval);
        Intent intent = getIntent();
        pet = (Pet) intent.getSerializableExtra("pet");
        userId = intent.getIntExtra("userId", -1);

        if ("Deer".equalsIgnoreCase(pet.getType())) {
            ivPetOval.setImageResource(R.drawable.baby_deer_default);
        } else {
            ivPetOval.setImageResource(R.drawable.baby_bear_default);
        }
    }
}
