package com.example.dears;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dears.data.model.Pet;

public class MainActivity extends AppCompatActivity {
    Pet pet;
    int userId;
    int timesChatted = 0;
    int timesFed = 0;
    int timesSleep = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        pet = (Pet) intent.getSerializableExtra("pet");
        userId = intent.getIntExtra("userId", -1);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        final Button button = findViewById(R.id.toChat);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ChatActivity.class));
            }
        });

        final Button jButton = findViewById(R.id.toJournal);
        jButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent jIntent = new Intent(MainActivity.this, JournalActivity.class);
                jIntent.putExtra("timesChatted", timesChatted);
                jIntent.putExtra("timesFed", timesFed);
                jIntent.putExtra("timesSleep", timesSleep);
                startActivity(jIntent);
            }
        });
    }
}