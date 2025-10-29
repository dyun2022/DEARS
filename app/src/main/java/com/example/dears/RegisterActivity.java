package com.example.dears;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class RegisterActivity extends AppCompatActivity {
    private String selectedPet = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button btnDeer = findViewById(R.id.btnDeer);
        Button btnBear = findViewById(R.id.btnBear);

        btnDeer.setOnClickListener(v -> {
            selectedPet = "Deer";
            btnDeer.setBackgroundColor(Color.parseColor("#A5D6A7"));
            btnBear.setBackgroundColor(Color.parseColor("#D3D3D3"));
        });

        btnBear.setOnClickListener(v -> {
            selectedPet = "Bear";
            btnBear.setBackgroundColor(Color.parseColor("#A5D6A7"));
            btnDeer.setBackgroundColor(Color.parseColor("#D3D3D3"));
        });

        findViewById(R.id.btnRegister).setOnClickListener(v -> {
            if (selectedPet == null) {
                Toast.makeText(this, "Please select a pet!", Toast.LENGTH_SHORT).show();
                return;
            }

            String username = ((EditText) findViewById(R.id.etUsername)).getText().toString().trim();
            String password = ((EditText) findViewById(R.id.etPassword)).getText().toString().trim();
            String birthday = ((EditText) findViewById(R.id.etBirthday)).getText().toString().trim();
            String petName = ((EditText) findViewById(R.id.etPetName)).getText().toString().trim();

            // TODO: Validate and send to backend /api/users/register
            Toast.makeText(this, "Registered as " + username + " with a " + selectedPet + "!", Toast.LENGTH_SHORT).show();
        });
    }
}
