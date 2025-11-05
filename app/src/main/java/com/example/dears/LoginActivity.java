package com.example.dears;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnHoverListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dears.data.api.APIClient;
import com.example.dears.data.api.InterfaceAPI;
import com.example.dears.data.model.Pet;
import com.example.dears.data.model.User;
import com.example.dears.data.request.changeUserRequest;
import com.example.dears.data.request.createPetRequest;
import com.example.dears.data.request.loginUserRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvSignup;
    InterfaceAPI interfaceAPI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        interfaceAPI = APIClient.getClient().create(InterfaceAPI.class);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin   = findViewById(R.id.btnLogin);
        tvSignup   = findViewById(R.id.tvSignup);

        btnLogin.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            String u = etUsername.getText().toString().trim();
            String p = etPassword.getText().toString().trim();
            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Username and password required", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUserRequest req1 = new loginUserRequest(u, p);
            final int[] userId = new int[1];

            Call<User> callRegister = interfaceAPI.loginUser(req1);
            callRegister.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        userId[0] = response.body().getUserID();
                    }
                    else if (response.code() == 401) {
                        Toast.makeText(LoginActivity.this, "Username or password is invalid", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Call<Pet> getPet = interfaceAPI.getPetById(userId[0]);

                    getPet.enqueue(new Callback<Pet>() {
                        @Override
                        public void onResponse(Call<Pet> call, Response<Pet> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Toast.makeText(LoginActivity.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, PetHomeActivity.class);
                                intent.putExtra("pet", response.body());
                                intent.putExtra("userId", userId[0]);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onFailure(Call<Pet> call, Throwable t) {
                            Toast.makeText(LoginActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                }
            });
        });

        tvSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            tvSignup.setOnHoverListener(underlineOnHover());
        }
        tvSignup.setOnFocusChangeListener((v, hasFocus) -> setUnderline((TextView) v, hasFocus));
        tvSignup.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) setUnderline((TextView) v, true);
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                setUnderline((TextView) v, false);
            return false;
        });
    }

    @RequiresApi(14)
    private OnHoverListener underlineOnHover() {
        return (v, event) -> {
            if (!(v instanceof TextView)) return false;
            boolean hovering = (event.getAction() == MotionEvent.ACTION_HOVER_ENTER);
            setUnderline((TextView) v, hovering);
            return false;
        };
    }

    private void setUnderline(TextView tv, boolean enabled) {
        if (enabled) {
            tv.setPaintFlags(tv.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
        } else {
            tv.setPaintFlags(tv.getPaintFlags() & ~android.graphics.Paint.UNDERLINE_TEXT_FLAG);
        }
    }
}
