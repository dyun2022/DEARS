package com.example.dears;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dears.data.model.Pet;

public class JournalActivity extends AppCompatActivity {
    int timesChatted;
    int timesFed;
    int timesSleep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        timesChatted = intent.getIntExtra("timesChatted", 0);
        timesFed = intent.getIntExtra("timesFed", 0);
        timesSleep = intent.getIntExtra("timesSleep", 0);
    }
}
