package com.example.dears;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.dears.data.api.APIClient;
import com.example.dears.data.api.InterfaceAPI;
import com.example.dears.data.model.Pet;
import com.example.dears.data.model.User;
import com.example.dears.data.request.changeUserRequest;
import com.example.dears.data.request.createPetRequest;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etBirthday, etPetName;
    private MaterialButton btnDeer, btnBear, btnRegister;
    private ImageView avatarBox;

    private String selectedPet = null;
    private int selectedAvatarResId = 0;
    private final AlertDialog[] alert = new AlertDialog[1];

    private static final int COLOR_GRAY  = Color.parseColor("#D9D9D9");
    private static final int COLOR_GREEN = Color.parseColor("#3F5743");
    private static final int COLOR_TEXT_DARK  = Color.parseColor("#000000");
    private static final int COLOR_TEXT_LIGHT = Color.parseColor("#FFFFFF");

    private InterfaceAPI api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        api = APIClient.getClient().create(InterfaceAPI.class);

        // Inputs
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etBirthday = findViewById(R.id.etBirthday);
        etPetName  = findViewById(R.id.etPetName);

        // Buttons & views
        btnDeer     = findViewById(R.id.btnDeer);
        btnBear     = findViewById(R.id.btnBear);
        btnRegister = findViewById(R.id.btnRegister);
        avatarBox   = findViewById(R.id.avatarBox);

        // Make Deer/Bear buttons show large icons
        setPetButtonAsLargeIcon(btnDeer, R.drawable.baby_deer_default, "Deer");
        setPetButtonAsLargeIcon(btnBear, R.drawable.baby_bear_default, "Bear");
        setPetButtonUnselected(btnDeer);
        setPetButtonUnselected(btnBear);

        btnDeer.setOnClickListener(v -> {
            selectedPet = "Deer";
            setPetButtonSelected(btnDeer);
            setPetButtonUnselected(btnBear);
        });

        btnBear.setOnClickListener(v -> {
            selectedPet = "Bear";
            setPetButtonSelected(btnBear);
            setPetButtonUnselected(btnDeer);
        });

        avatarBox.setOnClickListener(v -> showAvatarPickerDialog());

        btnRegister.setOnClickListener(v -> onRegisterClicked());
    }

    private void onRegisterClicked() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String birthdayStr = etBirthday.getText().toString().trim();
        String petName  = etPetName.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(birthdayStr) || TextUtils.isEmpty(petName)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedPet == null) {
            Toast.makeText(this, "Please select a pet (Deer or Bear)", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedAvatarResId == 0) {
            Toast.makeText(this, "Please select an avatar image", Toast.LENGTH_SHORT).show();
            return;
        }

        LocalDate birthday = parseBirthdayFlexible(birthdayStr);
        if (birthday == null) {
            Toast.makeText(this, "Birthday format must be YYYY-MM-DD or MM/DD/YYYY", Toast.LENGTH_LONG).show();
            return;
        }

        String avatarEntryName = safeEntryName(selectedAvatarResId);
        if (avatarEntryName == null) {
            Toast.makeText(this, "Invalid avatar image", Toast.LENGTH_SHORT).show();
            return;
        }

        changeUserRequest registerReq = new changeUserRequest(username, password, birthday, avatarEntryName);

        api.registerUser(registerReq).enqueue(new Callback<User>() {
            @Override public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    String msg = extractError(response);
                    Toast.makeText(RegisterActivity.this, "Register failed: " + msg, Toast.LENGTH_LONG).show();
                    return;
                }

                User created = response.body();
                int newUserId = created.getUserID();

                SharedPreferences sp = getSharedPreferences("auth", MODE_PRIVATE);
                sp.edit().putInt("userId", newUserId).apply();

                createPetRequest petReq = new createPetRequest(petName, selectedPet);
                api.createPet(newUserId, petReq).enqueue(new Callback<Pet>() {
                    @Override public void onResponse(Call<Pet> call2, Response<Pet> resp2) {
                        if (!resp2.isSuccessful() || resp2.body() == null) {
                            String msg = extractError(resp2);
                            Toast.makeText(RegisterActivity.this, "Pet create failed: " + msg, Toast.LENGTH_LONG).show();
                            return;
                        }
                        Intent intent = new Intent(RegisterActivity.this, PetHomeActivity.class);
                        intent.putExtra("userId", newUserId);
                        intent.putExtra("username", username);
                        intent.putExtra("birthday", birthday.toString());
                        intent.putExtra("avatarName", avatarEntryName);
                        intent.putExtra("pet", selectedPet);
                        startActivity(intent);
                    }

                    @Override public void onFailure(Call<Pet> call2, Throwable t2) {
                        Toast.makeText(RegisterActivity.this, "Pet create failed: " + t2.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Register failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String extractError(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                return response.errorBody().string();
            }
        } catch (IOException ignored) {}
        return "HTTP " + response.code();
    }

    @Nullable
    private LocalDate parseBirthdayFlexible(String s) {
        try {
            return LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE); // yyyy-MM-dd
        } catch (DateTimeParseException ignored) {}
        try {
            return LocalDate.parse(s, DateTimeFormatter.ofPattern("M/d/yyyy"));
        } catch (DateTimeParseException ignored) {}
        try {
            return LocalDate.parse(s, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } catch (DateTimeParseException ignored) {}
        return null;
    }

    private void setPetButtonAsLargeIcon(MaterialButton b, int iconRes, String label) {
        Drawable icon = ContextCompat.getDrawable(this, iconRes);
        b.setText(label);
        b.setTextColor(COLOR_TEXT_LIGHT);
        b.setIcon(icon);
        b.setIconTint(null);
        b.setIconSize(dp(96));
        b.setIconPadding(dp(6));
        b.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_TOP);
        b.setMinHeight(dp(140));
        b.setBackgroundTintList(ColorStateList.valueOf(COLOR_GRAY));
        b.setRippleColor(ColorStateList.valueOf(Color.parseColor("#22000000")));
    }

    private void setPetButtonSelected(MaterialButton b) {
        b.setBackgroundTintList(ColorStateList.valueOf(COLOR_GREEN));
        b.setTextColor(COLOR_TEXT_LIGHT);
    }

    private void setPetButtonUnselected(MaterialButton b) {
        b.setBackgroundTintList(ColorStateList.valueOf(COLOR_GRAY));
        b.setTextColor(COLOR_TEXT_DARK);
    }

    private int dp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(value * density);
    }

    private void showAvatarPickerDialog() {
        List<Integer> drawables = getAllPngDrawables();
        if (drawables.isEmpty()) {
            Toast.makeText(this, "No PNGs found in drawable/", Toast.LENGTH_SHORT).show();
            return;
        }

        ScrollView scroll = new ScrollView(this);
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(3);
        grid.setPadding(dp(12), dp(12), dp(12), dp(12));
        grid.setUseDefaultMargins(true);

        for (int resId : drawables) {
            ImageView iv = new ImageView(this);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setImageResource(resId);
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = dp(90);
            lp.height = dp(90);
            iv.setLayoutParams(lp);
            iv.setOnClickListener(v -> {
                selectedAvatarResId = resId;
                avatarBox.setImageResource(resId);
                if (alert[0] != null) alert[0].dismiss();
            });
            grid.addView(iv);
        }
        scroll.addView(grid, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Choose your avatar")
                .setView(scroll)
                .setNegativeButton("Cancel", (d, w) -> d.dismiss());

        alert[0] = builder.create();
        alert[0].show();
    }

    private List<Integer> getAllPngDrawables() {
        List<Integer> ids = new ArrayList<>();
        Field[] fields = R.drawable.class.getDeclaredFields();
        for (Field f : fields) {
            try {
                String name = f.getName();
                if (name.startsWith("ic_launcher")) continue;
                int resId = f.getInt(null);
                String type = getResources().getResourceTypeName(resId);
                if (!"drawable".equals(type)) continue;

                if (name.endsWith("_default") || name.endsWith("_happy") || name.endsWith("_sleep")
                        || name.contains("bear") || name.contains("deer")
                        || name.equals("berries") || name.equals("honey") || name.equals("salmon")
                        || name.equals("mushroom") || name.equals("bark")) {
                    ids.add(resId);
                }
            } catch (Exception ignored) {}
        }
        return ids;
    }

    private String safeEntryName(int resId) {
        try {
            return getResources().getResourceEntryName(resId);
        } catch (Exception e) {
            return null;
        }
    }
}
