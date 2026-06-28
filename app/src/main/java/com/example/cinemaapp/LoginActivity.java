package com.example.cinemaapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinemaapp.service.AuthService;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etPhone, etPassword;
    private TextView tvError;
    private Button btnLogin;
    private AuthService authService;
    private ImageView gengarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etPhone = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvError = findViewById(R.id.tvError);
        btnLogin = findViewById(R.id.btnLogin);
        gengarView = findViewById(R.id.gengarView);

        authService = new AuthService();
        animateViews();

        // Đặt nút đúng vị trí placeholder sau khi layout xong
        btnLogin.post(() -> {
            View placeholder = findViewById(R.id.btnPlaceholder);
            int[] loc = new int[2];
            placeholder.getLocationOnScreen(loc);
            int[] rootLoc = new int[2];
            findViewById(android.R.id.content).getLocationOnScreen(rootLoc);
            btnLogin.setX(loc[0] - rootLoc[0]);
            btnLogin.setY(loc[1] - rootLoc[1]);
        });

        // Gengar lắc lư khi nhập SĐT
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                float dir = (s.length() % 2 == 0) ? -8f : 8f;
                gengarView.animate().translationX(dir).setDuration(80)
                    .withEndAction(() -> gengarView.animate().translationX(0).setDuration(80).start())
                    .start();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Gengar lắc đầu khi nhập mật khẩu
        etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                gengarView.animate().rotation(-10f).setDuration(150)
                    .withEndAction(() -> gengarView.animate().rotation(10f).setDuration(150)
                    .withEndAction(() -> gengarView.animate().rotation(0f).setDuration(100).start())
                    .start()).start();
            }
        });

        btnLogin.setOnClickListener(v -> {
            if (!isFormFilled()) {
                runAwayButton(btnLogin);
                return;
            }
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100)
                .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(100)
                .withEndAction(this::handleLogin).start()).start();
        });

        findViewById(R.id.tvRegister).setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private boolean isFormFilled() {
        return !etPhone.getText().toString().trim().isEmpty()
            && !etPassword.getText().toString().trim().isEmpty();
    }

    private void runAwayButton(android.view.View btn) {
        android.view.View root = findViewById(android.R.id.content);
        float screenW = root.getWidth();
        float screenH = root.getHeight();
        float btnW = btn.getWidth();
        float btnH = btn.getHeight();

        // Vị trí gốc của nút (không có translation)
        float origX = btn.getLeft();
        float origY = btn.getTop();

        // Random translation tuyệt đối trong giới hạn màn hình
        float newTx = (float)(Math.random() * (screenW - btnW)) - origX;
        float newTy = (float)(Math.random() * (screenH - btnH)) - origY;

        btn.animate()
            .translationX(newTx)
            .translationY(newTy)
            .setDuration(350)
            .start();
    }

    private void animateViews() {
        View scrollView = findViewById(android.R.id.content);
        scrollView.setAlpha(0f);
        scrollView.animate().alpha(1f).setDuration(400).start();
    }

    private void handleLogin() {
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (phone.isEmpty() || password.isEmpty()) {
            showError("Vui lòng điền đầy đủ thông tin");
            return;
        }

        btnLogin.setEnabled(false);
        tvError.setVisibility(View.GONE);

        authService.login(phone, password, new AuthService.AuthCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Intent intent = new Intent(LoginActivity.this, com.example.cinemaapp.ui.CinemaShowtimeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    showError(message);
                    btnLogin.setEnabled(true);
                });
            }
        });
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }
}
