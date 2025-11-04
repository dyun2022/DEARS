package com.example.dears;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etBirthday, etPetName;
    private MaterialButton btnDeer, btnBear, btnRegister;
    private ImageView avatarBox;  // clickable avatar placeholder

    private String selectedPet = null;
    private int selectedAvatarResId = 0;

    // Colors
    private static final int COLOR_GRAY  = Color.parseColor("#D9D9D9");
    private static final int COLOR_GREEN = Color.parseColor("#3F5743");
    private static final int COLOR_TEXT_DARK  = Color.parseColor("#000000");
    private static final int COLOR_TEXT_LIGHT = Color.parseColor("#FFFFFF");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inputs
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etBirthday = findViewById(R.id.etBirthday);
        etPetName  = findViewById(R.id.etPetName);

        // Buttons & views
        btnDeer     = findViewById(R.id.btnDeer);
        btnBear     = findViewById(R.id.btnBear);
        btnRegister = findViewById(R.id.btnRegister);
        avatarBox   = findViewById(R.id.avatarBox); // make sure activity_register.xml uses ImageView for this id

        // ----- Make Deer/Bear images very large on the button -----
        setPetButtonAsLargeIcon(btnDeer, R.drawable.baby_deer_default, "Deer");
        setPetButtonAsLargeIcon(btnBear, R.drawable.baby_bear_default, "Bear");

        // Default unselected state
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

        // ----- Avatar picker: tap the gray box to choose ANY PNG in /drawable -----
        avatarBox.setOnClickListener(v -> showAvatarPickerDialog());

        // Submit → go to PetHomeActivity
        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String birthday = etBirthday.getText().toString().trim();
            String petName  = etPetName.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || birthday.isEmpty() || petName.isEmpty()) {
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

            // TODO: /api/users/register call here if desired.

            Intent intent = new Intent(RegisterActivity.this, PetHomeActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("pet", selectedPet);     // "Deer" or "Bear"
            intent.putExtra("petName", petName);
            intent.putExtra("birthday", birthday);
            intent.putExtra("avatarResId", selectedAvatarResId);
            startActivity(intent);
        });
    }

    // Make MaterialButton show a huge full-color icon that takes most of the button.
    private void setPetButtonAsLargeIcon(MaterialButton b, int iconRes, String label) {
        Drawable icon = ContextCompat.getDrawable(this, iconRes);
        b.setText(label);
        b.setTextColor(COLOR_TEXT_LIGHT);                 // will be adjusted on select/unselect
        b.setIcon(icon);                                  // full-color icon
        b.setIconTint(null);                              // IMPORTANT: keep full colors
        b.setIconSize(dp(96));                            // << large icon
        b.setIconPadding(dp(6));
        b.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_TOP); // icon on top, text below
        b.setMinHeight(dp(140));                          // taller button to fit icon
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

    // Avatar picker: reflect over R.drawable to list all PNGs; show a grid; pick one.
    private void showAvatarPickerDialog() {
        List<Integer> drawables = getAllPngDrawables();
        if (drawables.isEmpty()) {
            Toast.makeText(this, "No PNGs found in drawable/", Toast.LENGTH_SHORT).show();
            return;
        }

        // Build a simple grid programmatically
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
                avatarBox.setImageResource(resId); // preview on the register page
                // dismiss dialog
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

        // Small trick to dismiss from inside item click:
        alert[0] = builder.create();
        alert[0].show();
    }

    // Keep a single-element array to allow access inside lambda:
    private final AlertDialog[] alert = new AlertDialog[1];

    // Collect *all* PNG drawables via reflection (filters out launcher/shape xmls)
    private List<Integer> getAllPngDrawables() {
        List<Integer> ids = new ArrayList<>();
        Field[] fields = R.drawable.class.getDeclaredFields();
        for (Field f : fields) {
            try {
                // Skip auto stuff that isn’t your content
                String name = f.getName();
                if (name.startsWith("ic_launcher")) continue; // skip launcher
                // Using getInt(null) on R.* is safe
                int resId = f.getInt(null);
                String type = getResources().getResourceTypeName(resId);
                if (!"drawable".equals(type)) continue;

                // Only include png-backed drawables if possible. We approximate by name;
                // your content uses .png names like baby_*, teen_*, adult_*, berries/honey/salmon etc.
                // If you want *absolutely* all drawables, remove this name filter.
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
