package com.example.dears;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dears.data.api.APIClient;
import com.example.dears.data.api.InterfaceAPI;
import com.example.dears.data.model.User;
import com.google.android.material.button.MaterialButton;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
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
    private MaterialButton btnSave;
    private MaterialButton btnLogout; // ensure this exists in your XML

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
        btnLogout  = findViewById(R.id.btnLogout); // add this to XML under Save
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

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                // clear session + pet UI state and go to Login
                getSharedPreferences("auth", MODE_PRIVATE).edit().clear().apply();
                getSharedPreferences("PetPrefs", MODE_PRIVATE).edit().clear().apply();
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finishAffinity();
            });
        }
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

        final String newUsername   = etUsername.getText().toString().trim();
        final String newPassword   = etPassword.getText().toString().trim();
        final String newBirthday   = etBirthday.getText().toString().trim();
        final String newAvatarName = (selectedAvatarResId != 0)
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

        final boolean changePassword = !TextUtils.isEmpty(newPassword);
        final boolean changeBirthday = (initialBirthday == null) || !newBirthday.equals(initialBirthday);
        final boolean changeAvatar   = (newAvatarName != null) &&
                ((initialAvatarName == null) || !newAvatarName.equals(initialAvatarName));
        final boolean changeUsername = (initialUsername == null) || !newUsername.equals(initialUsername);

        if (!changePassword && !changeBirthday && !changeAvatar && !changeUsername) {
            Toast.makeText(this, "No changes to save.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Build a sequential chain of updates to avoid concurrent lost updates.
        final Deque<Runnable> chain = new ArrayDeque<>();

        if (changePassword) {
            chain.add(() -> doUpdatePassword(newPassword, succeed(chain)));
        }
        if (changeBirthday) {
            chain.add(() -> doUpdateBirthday(newBirthday, succeed(chain)));
        }
        if (changeAvatar) {
            chain.add(() -> doUpdateAvatar(newAvatarName, succeed(chain)));
        }
        if (changeUsername) {
            chain.add(() -> doUpdateUsername(newUsername, succeed(chain)));
        }

        // start chain
        chain.poll().run();
    }

    /** Returns a continuation that runs the next step or finishes on success. */
    private Runnable succeed(Deque<Runnable> chain) {
        return () -> {
            Runnable next = chain.poll();
            if (next != null) {
                next.run();
            } else {
                etPassword.setText("");
                Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
                finish();
            }
        };
    }

    private void doUpdatePassword(String newPassword, Runnable onSuccess) {
        Map<String, String> body = new HashMap<>();
        body.put("password", newPassword);
        api.updatePassword(userId, body).enqueue(new Callback<User>() {
            @Override public void onResponse(Call<User> call, Response<User> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    showFail("Password");
                    return;
                }
                serverPasswordShadow = newPassword;
                onSuccess.run();
            }
            @Override public void onFailure(Call<User> call, Throwable t) { showFail("Password"); }
        });
    }

    private void doUpdateBirthday(String newBirthday, Runnable onSuccess) {
        Map<String, String> body = new HashMap<>();
        body.put("birthday", newBirthday); // ISO-8601 string expected by server
        api.updateBirthday(userId, body).enqueue(new Callback<User>() {
            @Override public void onResponse(Call<User> call, Response<User> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    showFail("Birthday");
                    return;
                }
                initialBirthday = newBirthday;
                onSuccess.run();
            }
            @Override public void onFailure(Call<User> call, Throwable t) { showFail("Birthday"); }
        });
    }

    private void doUpdateAvatar(String newAvatarName, Runnable onSuccess) {
        Map<String, String> body = new HashMap<>();
        body.put("avatar", newAvatarName);
        api.updateAvatar(userId, body).enqueue(new Callback<User>() {
            @Override public void onResponse(Call<User> call, Response<User> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    showFail("Avatar");
                    return;
                }
                initialAvatarName = newAvatarName;
                onSuccess.run();
            }
            @Override public void onFailure(Call<User> call, Throwable t) { showFail("Avatar"); }
        });
    }

    private void doUpdateUsername(String newUsername, Runnable onSuccess) {
        Map<String, String> body = new HashMap<>();
        body.put("username", newUsername);
        // Requires InterfaceAPI to have: @PUT("users/{id}/username") Call<User> updateUsername(@Path("id") int userId, @Body Map<String, String> body);
        api.updateUsername(userId, body).enqueue(new Callback<User>() {
            @Override public void onResponse(Call<User> call, Response<User> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    showFail("Username");
                    return;
                }
                initialUsername = resp.body().getUsername();
                getSharedPreferences("auth", MODE_PRIVATE).edit()
                        .putString("username", initialUsername)
                        .apply();
                onSuccess.run();
            }
            @Override public void onFailure(Call<User> call, Throwable t) { showFail("Username"); }
        });
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

    private void showFail(String which) {
        Toast.makeText(this, which + " update failed", Toast.LENGTH_LONG).show();
    }
}
