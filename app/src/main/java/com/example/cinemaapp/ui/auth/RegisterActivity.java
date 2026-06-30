package com.example.cinemaapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinemaapp.R;
import com.example.cinemaapp.data.model.User;
import com.example.cinemaapp.service.AuthService;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPhone, etPassword, etConfirmPassword;
    private TextView tvError;
    private Button btnRegister;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        tvError = findViewById(R.id.tvError);
        btnRegister = findViewById(R.id.btnRegister);

        authService = new AuthService();

        // Đặt nút đúng vị trí placeholder
        btnRegister.post(() -> {
            View placeholder = findViewById(R.id.btnPlaceholder);
            int[] loc = new int[2];
            placeholder.getLocationOnScreen(loc);
            int[] rootLoc = new int[2];
            findViewById(android.R.id.content).getLocationOnScreen(rootLoc);
            btnRegister.setX(loc[0] - rootLoc[0]);
            btnRegister.setY(loc[1] - rootLoc[1]);
        });

        btnRegister.setOnClickListener(v -> {
            if (etName.getText().toString().trim().isEmpty()
                || etEmail.getText().toString().trim().isEmpty()
                || etPhone.getText().toString().trim().isEmpty()
                || etPassword.getText().toString().trim().isEmpty()
                || etConfirmPassword.getText().toString().trim().isEmpty()) {
                runAwayButton(v);
                return;
            }
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100)
                .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(100)
                .withEndAction(this::handleRegister).start()).start();
        });

        findViewById(R.id.tvLogin).setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }

    private void runAwayButton(android.view.View btn) {
        android.view.View root = findViewById(android.R.id.content);
        float screenW = root.getWidth();
        float screenH = root.getHeight();
        float btnW = btn.getWidth();
        float btnH = btn.getHeight();

        float origX = btn.getLeft();
        float origY = btn.getTop();

        float newTx = (float)(Math.random() * (screenW - btnW)) - origX;
        float newTy = (float)(Math.random() * (screenH - btnH)) - origY;

        btn.animate()
            .translationX(newTx)
            .translationY(newTy)
            .setDuration(350)
            .start();
    }

    private void handleRegister() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            showError("Vui lòng điền đầy đủ thông tin");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Email không hợp lệ");
            return;
        }
        if (password.length() < 6) {
            showError("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showError("Mật khẩu xác nhận không khớp");
            return;
        }

        btnRegister.setEnabled(false);
        tvError.setVisibility(View.GONE);

        User user = new User();
        user.name = name;
        user.email = email;
        user.phone = phone;
        user.password = password;

        authService.signUp(user, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    showError(message);
                    btnRegister.setEnabled(true);
                });
            }
        });
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }
}
