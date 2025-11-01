package com.example.dears;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dears.data.api.InterfaceAPI;
import com.example.dears.data.api.APIClient;
import com.example.dears.data.model.Pet;
import com.example.dears.data.model.User;
import com.example.dears.data.request.changeUserRequest;
import com.example.dears.data.request.createPetRequest;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterActivity extends AppCompatActivity {
    private String selectedPet = null;
    InterfaceAPI interfaceAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        interfaceAPI = APIClient.getClient().create(InterfaceAPI.class);

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

            // Validation
            if (username.isEmpty()) {
                Toast.makeText(this, "Username is required!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.isEmpty()) {
                Toast.makeText(this, "Password is required!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (birthday.isEmpty()) {
                Toast.makeText(this, "Birthday is required!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (petName.isEmpty()) {
                Toast.makeText(this, "Pet name is required!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Correct date format
            LocalDate birthdate;
            try {
                birthdate = LocalDate.parse(birthday);
            } catch (DateTimeParseException e) {
                Toast.makeText(this, "Please write birthday as YYYY-MM-DD!", Toast.LENGTH_SHORT).show();
                return;
            }
            // TODO: Validate and send to backend /api/users/register
            // TODO: allow avatar to be chosen
            changeUserRequest req1 = new changeUserRequest(username, password, birthdate, "\uD83C\uDF6F");
            final int[] userId = new int[1];

            Call<User> callRegister = interfaceAPI.registerUser(req1);
            callRegister.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        userId[0] = response.body().getUserID();
                    }
                    else if (response.code() == 409) {
                        Toast.makeText(RegisterActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                }
            });

            createPetRequest req2 = new createPetRequest(selectedPet, petName);
            Call<Pet> callPetCreation = interfaceAPI.createPet(userId[0], req2);

            callPetCreation.enqueue(new Callback<Pet>() {
                @Override
                public void onResponse(Call<Pet> call, Response<Pet> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(RegisterActivity.this, "Registered as " + username + " with a " + selectedPet + "!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.putExtra("pet", response.body());
                        intent.putExtra("userId", userId);
                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<Pet> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
