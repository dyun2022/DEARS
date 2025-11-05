package com.example.dears;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dears.data.api.APIClient;
import com.example.dears.data.api.InterfaceAPI;
import com.example.dears.data.model.User;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etBirthday;
    private ImageView avatarBox;
    private ImageButton btnBack;
    private android.widget.Button btnSave;

    private int userId = -1;
    private String initialUsername;
    private String initialBirthday;
    private String initialAvatarName;

    private int selectedAvatarResId = 0;

    private final AlertDialog[] avatarDialog = new AlertDialog[1];

    private InterfaceAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        api = APIClient.getClient().create(InterfaceAPI.class);

        btnBack    = findViewById(R.id.btnBack);
        btnSave    = findViewById(R.id.btnSave);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etBirthday = findViewById(R.id.etBirthday);
        avatarBox  = findViewById(R.id.avatarBox);

        // Read latest values from PetHomeActivity
        userId            = getIntent().getIntExtra("userId", getIntent().getIntExtra("userID", -1));
        initialUsername   = getIntent().getStringExtra("username");
        initialBirthday   = getIntent().getStringExtra("birthday");
        initialAvatarName = getIntent().getStringExtra("avatarName");

        if (initialUsername != null) etUsername.setText(initialUsername);
        if (initialBirthday != null) etBirthday.setText(initialBirthday);
        if (initialAvatarName != null) {
            int res = getResources().getIdentifier(initialAvatarName, "drawable", getPackageName());
            if (res != 0) {
                selectedAvatarResId = res;
                avatarBox.setImageResource(res);
            }
        }

        btnBack.setOnClickListener(v -> onBackPressed());
        avatarBox.setOnClickListener(v -> showAvatarPicker());

        btnSave.setOnClickListener(v -> onSave());
    }

    private void onSave() {
        if (userId <= 0) {
            Toast.makeText(this, "Missing user ID. Cannot save.", Toast.LENGTH_LONG).show();
            return;
        }
        String newUsername = etUsername.getText().toString().trim();
        String newPassword = etPassword.getText().toString().trim();
        String newBirthday = etBirthday.getText().toString().trim();
        String newAvatarName = (selectedAvatarResId != 0)
                ? getResources().getResourceEntryName(selectedAvatarResId)
                : null;

        if (TextUtils.isEmpty(newUsername)) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(newBirthday)) {
            Toast.makeText(this, "Birthday cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        AtomicInteger pending = new AtomicInteger(0);
        AtomicInteger done = new AtomicInteger(0);
        AtomicBoolean failed = new AtomicBoolean(false);

        Runnable tryFinish = () -> {
            if (done.get() == pending.get() && pending.get() > 0 && !failed.get()) {
                Toast.makeText(SettingsActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                finish();
            }
        };

        // 1) Username (hit /api/users/{id}/username)
        if (initialUsername == null || !initialUsername.equals(newUsername)) {
            pending.incrementAndGet();
            Map<String, String> body = new HashMap<>();
            body.put("username", newUsername);
            api.updateUsername(userId, body).enqueue(new Callback<User>() {
                @Override public void onResponse(Call<User> call, Response<User> response) {
                    if (!response.isSuccessful()) {
                        failed.set(true);
                        Toast.makeText(SettingsActivity.this,
                                "Username update failed (HTTP " + response.code() + ")",
                                Toast.LENGTH_LONG).show();
                    } else {
                        initialUsername = newUsername; // keep local state in sync
                    }
                    done.incrementAndGet();
                    tryFinish.run();
                }
                @Override public void onFailure(Call<User> call, Throwable t) {
                    failed.set(true);
                    Toast.makeText(SettingsActivity.this,
                            "Username update failed: " + t.getMessage(),
                            Toast.LENGTH_LONG).show();
                    done.incrementAndGet();
                    tryFinish.run();
                }
            });
        }

        // 2) Password (only if provided)
        if (!TextUtils.isEmpty(newPassword)) {
            pending.incrementAndGet();
            Map<String, String> body = new HashMap<>();
            body.put("password", newPassword);
            api.updatePassword(userId, body).enqueue(new Callback<User>() {
                @Override public void onResponse(Call<User> call, Response<User> response) {
                    if (!response.isSuccessful()) {
                        failed.set(true);
                        Toast.makeText(SettingsActivity.this,
                                "Password update failed (HTTP " + response.code() + ")",
                                Toast.LENGTH_LONG).show();
                    }
                    done.incrementAndGet();
                    tryFinish.run();
                }
                @Override public void onFailure(Call<User> call, Throwable t) {
                    failed.set(true);
                    Toast.makeText(SettingsActivity.this,
                            "Password update failed: " + t.getMessage(),
                            Toast.LENGTH_LONG).show();
                    done.incrementAndGet();
                    tryFinish.run();
                }
            });
        }

        // 3) Birthday (if changed)
        if (initialBirthday == null || !initialBirthday.equals(newBirthday)) {
            pending.incrementAndGet();
            Map<String, Object> body = new HashMap<>();
            body.put("birthday", newBirthday); // ISO yyyy-MM-dd; backend maps to LocalDate
            api.updateBirthday(userId, body).enqueue(new Callback<User>() {
                @Override public void onResponse(Call<User> call, Response<User> response) {
                    if (!response.isSuccessful()) {
                        failed.set(true);
                        Toast.makeText(SettingsActivity.this,
                                "Birthday update failed (HTTP " + response.code() + ")",
                                Toast.LENGTH_LONG).show();
                    } else {
                        initialBirthday = newBirthday;
                    }
                    done.incrementAndGet();
                    tryFinish.run();
                }
                @Override public void onFailure(Call<User> call, Throwable t) {
                    failed.set(true);
                    Toast.makeText(SettingsActivity.this,
                            "Birthday update failed: " + t.getMessage(),
                            Toast.LENGTH_LONG).show();
                    done.incrementAndGet();
                    tryFinish.run();
                }
            });
        }

        // 4) Avatar (if changed)
        if (newAvatarName != null && (initialAvatarName == null || !initialAvatarName.equals(newAvatarName))) {
            pending.incrementAndGet();
            Map<String, String> body = new HashMap<>();
            body.put("avatar", newAvatarName);
            api.updateAvatar(userId, body).enqueue(new Callback<User>() {
                @Override public void onResponse(Call<User> call, Response<User> response) {
                    if (!response.isSuccessful()) {
                        failed.set(true);
                        Toast.makeText(SettingsActivity.this,
                                "Avatar update failed (HTTP " + response.code() + ")",
                                Toast.LENGTH_LONG).show();
                    } else {
                        initialAvatarName = newAvatarName;
                    }
                    done.incrementAndGet();
                    tryFinish.run();
                }
                @Override public void onFailure(Call<User> call, Throwable t) {
                    failed.set(true);
                    Toast.makeText(SettingsActivity.this,
                            "Avatar update failed: " + t.getMessage(),
                            Toast.LENGTH_LONG).show();
                    done.incrementAndGet();
                    tryFinish.run();
                }
            });
        }

        if (pending.get() == 0) {
            Toast.makeText(this, "No changes to save.", Toast.LENGTH_SHORT).show();
        }
    }

    /* ---------- Avatar picker ---------- */
    private void showAvatarPicker() {
        List<Integer> ids = listContentDrawables();
        if (ids.isEmpty()) {
            Toast.makeText(this, "No PNGs found in drawable/", Toast.LENGTH_SHORT).show();
            return;
        }
        android.widget.ScrollView scroll = new android.widget.ScrollView(this);
        android.widget.GridLayout grid = new android.widget.GridLayout(this);
        grid.setColumnCount(3);
        int pad = dp(12);
        grid.setPadding(pad, pad, pad, pad);
        grid.setUseDefaultMargins(true);

        for (int resId : ids) {
            ImageView iv = new ImageView(this);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setImageResource(resId);
            android.widget.GridLayout.LayoutParams lp = new android.widget.GridLayout.LayoutParams();
            lp.width = dp(90);
            lp.height = dp(90);
            iv.setLayoutParams(lp);
            iv.setOnClickListener(v -> {
                selectedAvatarResId = resId;
                avatarBox.setImageResource(resId);
                if (avatarDialog[0] != null) avatarDialog[0].dismiss();
            });
            grid.addView(iv);
        }
        scroll.addView(grid, new android.widget.ScrollView.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

        AlertDialog.Builder b = new AlertDialog.Builder(this)
                .setTitle("Choose your avatar")
                .setView(scroll)
                .setNegativeButton("Cancel", (d, w) -> d.dismiss());
        avatarDialog[0] = b.create();
        avatarDialog[0].show();
    }

    private List<Integer> listContentDrawables() {
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

    private int dp(int value) {
        float d = getResources().getDisplayMetrics().density;
        return Math.round(value * d);
    }
}
