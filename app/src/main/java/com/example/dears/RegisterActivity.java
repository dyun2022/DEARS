package com.example.dears;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.dears.data.api.APIClient;
import com.example.dears.data.api.InterfaceAPI;
import com.example.dears.data.model.Pet;
import com.example.dears.data.model.User;
import com.example.dears.data.request.changeUserRequest;
import com.example.dears.data.request.createPetRequest;
import com.google.android.material.button.MaterialButton;

import java.lang.reflect.Field;
import java.time.LocalDate;
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
    private String selectedAvatarName = null;

    private final AlertDialog[] alert = new AlertDialog[1];

    private static final int COLOR_GRAY  = Color.parseColor("#D9D9D9");
    private static final int COLOR_GREEN = Color.parseColor("#3F5743");
    private static final int COLOR_TEXT_DARK  = Color.parseColor("#000000");
    private static final int COLOR_TEXT_LIGHT = Color.parseColor("#FFFFFF");

    private InterfaceAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        api = APIClient.getClient().create(InterfaceAPI.class);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etBirthday = findViewById(R.id.etBirthday);
        etPetName  = findViewById(R.id.etPetName);

        btnDeer     = findViewById(R.id.btnDeer);
        btnBear     = findViewById(R.id.btnBear);
        btnRegister = findViewById(R.id.btnRegister);
        avatarBox   = findViewById(R.id.avatarBox);

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

        if (username.isEmpty() || password.isEmpty() || birthdayStr.isEmpty() || petName.isEmpty()) {
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

        // birthday to LocalDate
        final LocalDate birthday;
        try {
            birthday = LocalDate.parse(birthdayStr); // expects yyyy-MM-dd
        } catch (DateTimeParseException e) {
            Toast.makeText(this, "Birthday must be yyyy-MM-dd", Toast.LENGTH_SHORT).show();
            return;
        }

        // resource entry name for backend avatar field
        selectedAvatarName = getResources().getResourceEntryName(selectedAvatarResId);

        // Call backend register (your InterfaceAPI has users/register that returns User)
        changeUserRequest req = new changeUserRequest(username, password, birthday, selectedAvatarName);
        api.registerUser(req).enqueue(new Callback<User>() {
            @Override public void onResponse(Call<User> call, Response<User> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    Toast.makeText(RegisterActivity.this, "Register failed", Toast.LENGTH_SHORT).show();
                    return;
                }
                User created = resp.body();
                int userId = created.getUserID();

                // (Optional) ensure pet exists for this user
                createPetRequest petReq = new createPetRequest(selectedPet, petName);
                api.createPet(userId, petReq).enqueue(new Callback<Pet>() {
                    @Override public void onResponse(Call<Pet> call2, Response<Pet> resp2) {
                        // Even if pet creation fails, still proceed to PetHome with the correct userId.
                        goToPetHome(userId, username, birthdayStr, selectedAvatarName, selectedPet);
                    }
                    @Override public void onFailure(Call<Pet> call2, Throwable t2) {
                        goToPetHome(userId, username, birthdayStr, selectedAvatarName, selectedPet);
                    }
                });
            }
            @Override public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Register failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToPetHome(int userId, String username, String birthday, String avatarName, String petType) {
        Intent intent = new Intent(RegisterActivity.this, PetHomeActivity.class);
        // CRITICAL: carry userId forward so Settings can update correctly
        intent.putExtra("userId", userId);
        intent.putExtra("username", username);
        intent.putExtra("birthday", birthday);
        intent.putExtra("avatarName", avatarName);
        intent.putExtra("pet", petType); // "Deer" or "Bear" to show the right oval art
        startActivity(intent);
        // Optionally finish register so back doesn’t return here
        // finish();
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
                alert[0].dismiss();
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
}
