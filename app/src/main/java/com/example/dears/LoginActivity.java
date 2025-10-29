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

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
            // TODO: call /api/users/login here (Retrofit/OkHttp). For now:
            Toast.makeText(this, "Logging in…", Toast.LENGTH_SHORT).show();
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
