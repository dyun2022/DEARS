package com.example.dears;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dears.data.api.APIClient;
import com.example.dears.data.api.InterfaceAPI;
import com.example.dears.data.model.User;
import com.google.android.material.button.MaterialButton;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etBirthday;
    private ImageView avatarBox;
    private ImageButton btnBack;
    private MaterialButton btnSave, btnLogout;

    private InterfaceAPI api;
    private int userId = -1;

    private String initialUsername, initialAvatarName, initialBirthday;
    private String serverPasswordShadow;
    private int selectedAvatarResId = 0;

    private final AlertDialog[] avatarDialog = new AlertDialog[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        api = APIClient.getClient().create(InterfaceAPI.class);

        btnBack    = findViewById(R.id.btnBack);
        btnSave    = findViewById(R.id.btnSave);
        btnLogout  = findViewById(R.id.btnLogout);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etBirthday = findViewById(R.id.etBirthday);
        avatarBox  = findViewById(R.id.avatarBox);

        userId = getIntent().getIntExtra("userId", -1);
        if (userId <= 0) {
            SharedPreferences sp = getSharedPreferences("auth", MODE_PRIVATE);
            userId = sp.getInt("userId", -1);
        }

        fastPrefillFromIntentOrPrefs();

        if (userId > 0) {
            fetchAndPrefillFromServer(userId);
        }

        btnBack.setOnClickListener(v -> onBackPressed());
        avatarBox.setOnClickListener(v -> showAvatarPickerDialog());
        btnSave.setOnClickListener(v -> saveChanges());

        btnLogout.setOnClickListener(v -> {
            getSharedPreferences("auth", MODE_PRIVATE).edit().clear().apply();
            getSharedPreferences("PetPrefs", MODE_PRIVATE).edit().clear().apply();

            Intent i = new Intent(SettingsActivity.this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finishAffinity();
        });
    }

    private void fastPrefillFromIntentOrPrefs() {
        String u = getIntent().getStringExtra("username");
        String b = getIntent().getStringExtra("birthday");
        String avatarName = getIntent().getStringExtra("avatarName");

        SharedPreferences sp = getSharedPreferences("auth", MODE_PRIVATE);
        if (TextUtils.isEmpty(u)) u = sp.getString("username", null);
        if (TextUtils.isEmpty(b)) b = sp.getString("birthday", null);
        if (TextUtils.isEmpty(avatarName)) avatarName = sp.getString("avatarName", null);

        if (!TextUtils.isEmpty(u)) etUsername.setText(u);
        if (!TextUtils.isEmpty(b)) etBirthday.setText(b);
        if (!TextUtils.isEmpty(avatarName)) {
            int id = getResources().getIdentifier(avatarName, "drawable", getPackageName());
            if (id != 0) {
                selectedAvatarResId = id;
                avatarBox.setImageResource(id);
            }
        }
    }

    private void fetchAndPrefillFromServer(int uid) {
        api.getUserById(uid).enqueue(new Callback<User>() {
            @Override public void onResponse(Call<User> call, Response<User> resp) {
                if (!resp.isSuccessful() || resp.body() == null) return;
                User u = resp.body();

                if (!TextUtils.isEmpty(u.getUsername())) {
                    etUsername.setText(u.getUsername());
                    initialUsername = u.getUsername();
                }
                if (u.getBirthday() != null) {
                    etBirthday.setText(u.getBirthday().toString());
                    initialBirthday = u.getBirthday().toString();
                }
                serverPasswordShadow = u.getPassword();

                String avatarEntry = u.getAvatar();
                if (!TextUtils.isEmpty(avatarEntry)) {
                    int id = getResources().getIdentifier(avatarEntry, "drawable", getPackageName());
                    if (id != 0) {
                        selectedAvatarResId = id;
                        avatarBox.setImageResource(id);
                        initialAvatarName = avatarEntry;
                    }
                }

                SharedPreferences sp = getSharedPreferences("auth", MODE_PRIVATE);
                sp.edit()
                        .putString("username", u.getUsername())
                        .putString("birthday", (u.getBirthday() != null ? u.getBirthday().toString() : null))
                        .putString("avatarName", u.getAvatar())
                        .apply();
            }
            @Override public void onFailure(Call<User> call, Throwable t) {}
        });
    }

    private void saveChanges() {
        if (userId <= 0) {
            Toast.makeText(this, "Missing user ID", Toast.LENGTH_SHORT).show();
            return;
        }

        String newUsername   = etUsername.getText().toString().trim();
        String newPassword   = etPassword.getText().toString().trim();
        String newBirthday   = etBirthday.getText().toString().trim();
        String newAvatarName = (selectedAvatarResId != 0)
                ? getResources().getResourceEntryName(selectedAvatarResId)
                : initialAvatarName;

        if (TextUtils.isEmpty(newUsername)) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(newBirthday)) {
            Toast.makeText(this, "Birthday cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        final int[] pending = {0};
        final int[] done    = {0};
        final boolean[] failed = {false};

        Runnable maybeFinish = () -> {
            if (done[0] == pending[0] && pending[0] > 0 && !failed[0]) {
                etPassword.setText("");
                Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
                finish();
            }
        };

        // password
        if (!TextUtils.isEmpty(newPassword)) {
            pending[0]++;
            Map<String, String> body = new HashMap<>();
            body.put("password", newPassword);
            api.updatePassword(userId, body).enqueue(new Callback<User>() {
                @Override public void onResponse(Call<User> call, Response<User> resp) {
                    if (!resp.isSuccessful()) failed[0] = true;
                    done[0]++; maybeFinish.run();
                }
                @Override public void onFailure(Call<User> call, Throwable t) {
                    failed[0] = true; done[0]++; maybeFinish.run();
                }
            });
        }

        if (initialBirthday == null || !initialBirthday.equals(newBirthday)) {
            pending[0]++;
            Map<String, String> body = new HashMap<>();
            body.put("birthday", newBirthday);
            api.updateBirthday(userId, body).enqueue(new Callback<User>() {
                @Override public void onResponse(Call<User> call, Response<User> resp) {
                    if (!resp.isSuccessful()) { failed[0] = true; }
                    else { initialBirthday = newBirthday; }
                    done[0]++; maybeFinish.run();
                }
                @Override public void onFailure(Call<User> call, Throwable t) {
                    failed[0] = true; done[0]++; maybeFinish.run();
                }
            });
        }

        // avatar
        if (newAvatarName != null && (initialAvatarName == null || !initialAvatarName.equals(newAvatarName))) {
            pending[0]++;
            Map<String, String> body = new HashMap<>();
            body.put("avatar", newAvatarName);
            api.updateAvatar(userId, body).enqueue(new Callback<User>() {
                @Override public void onResponse(Call<User> call, Response<User> resp) {
                    if (!resp.isSuccessful()) { failed[0] = true; }
                    else { initialAvatarName = newAvatarName; }
                    done[0]++; maybeFinish.run();
                }
                @Override public void onFailure(Call<User> call, Throwable t) {
                    failed[0] = true; done[0]++; maybeFinish.run();
                }
            });
        }

        if (initialUsername == null || !initialUsername.equals(newUsername)) {
            pending[0]++;

            Map<String, String> body = new HashMap<>();
            body.put("username", newUsername);

            Log.d("Settings", "Calling updateUsername for userId=" + userId + " -> " + newUsername);

            api.updateUsername(userId, body).enqueue(new Callback<User>() {
                @Override public void onResponse(Call<User> call, Response<User> resp) {
                    if (!resp.isSuccessful() || resp.body() == null) {
                        failed[0] = true;
                        String msg = "Username update failed (HTTP " + resp.code() + ")";
                        if (resp.code() == 404) msg = "Username update failed: user not found (check userId)";
                        if (resp.code() == 409) msg = "Username update failed: that username is already taken";
                        Toast.makeText(SettingsActivity.this, msg, Toast.LENGTH_LONG).show();
                    } else {
                        User u = resp.body();
                        initialUsername = u.getUsername();
                        etUsername.setText(u.getUsername());
                        getSharedPreferences("auth", MODE_PRIVATE)
                                .edit()
                                .putString("username", u.getUsername())
                                .apply();
                    }
                    done[0]++; maybeFinish.run();
                }

                @Override public void onFailure(Call<User> call, Throwable t) {
                    failed[0] = true;
                    Toast.makeText(SettingsActivity.this, "Username update failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    done[0]++; maybeFinish.run();
                }
            });
        }

        if (pending[0] == 0) {
            Toast.makeText(this, "No changes to save.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAvatarPickerDialog() {
        List<Integer> drawables = getAllCandidateDrawables();
        if (drawables.isEmpty()) {
            Toast.makeText(this, "No PNGs found in drawable/", Toast.LENGTH_SHORT).show();
            return;
        }

        ScrollView scroll = new ScrollView(this);
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(3);
        int pad = dp(12);
        grid.setPadding(pad, pad, pad, pad);
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
                if (avatarDialog[0] != null) avatarDialog[0].dismiss();
            });
            grid.addView(iv);
        }
        scroll.addView(grid, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Choose your avatar")
                .setView(scroll)
                .setNegativeButton("Cancel", (d, w) -> d.dismiss());

        avatarDialog[0] = builder.create();
        avatarDialog[0].show();
    }

    private List<Integer> getAllCandidateDrawables() {
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
