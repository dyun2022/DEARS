package com.example.dears;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dears.data.model.Pet;

public class JournalActivity extends AppCompatActivity {
    Pet pet;
    int userId;
    int timesChatted;
    int timesFed;
    int timesSleep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        pet = (Pet) intent.getSerializableExtra("pet");
        userId = intent.getIntExtra("userId", -1);
        timesChatted = intent.getIntExtra("timesChatted", 0);
        timesFed = intent.getIntExtra("timesFed", 0);
        timesSleep = intent.getIntExtra("timesSleep", 0);
    }
}
