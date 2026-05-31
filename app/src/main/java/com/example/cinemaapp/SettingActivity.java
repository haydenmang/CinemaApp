package com.example.cinemaapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingActivity extends AppCompatActivity {

    private com.google.android.material.textfield.TextInputEditText edtName, edtPhone, edtDob;
    private android.widget.AutoCompleteTextView spinGender, spinCinema, spinProvince, spinDistrict;
    private com.google.android.material.textfield.TextInputEditText edtCurrentPassword, edtNewPassword, edtConfirmPassword;
    private com.google.android.material.button.MaterialButton btnUpdate, btnChangePassword;
    private final com.example.cinemaapp.data.repository.UserRepository userRepository = new com.example.cinemaapp.data.repository.UserRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtDob = findViewById(R.id.edtDob);
        spinGender = findViewById(R.id.spinGender);
        spinCinema = findViewById(R.id.spinCinema);
        spinProvince = findViewById(R.id.spinProvince);
        spinDistrict = findViewById(R.id.spinDistrict);
        
        edtCurrentPassword = findViewById(R.id.edtCurrentPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        loadUserData();

        btnUpdate.setOnClickListener(v -> updateUserInfo());
        btnChangePassword.setOnClickListener(v -> changePassword());
    }

    private void loadUserData() {
        if (com.example.cinemaapp.data.repository.UserRepository.currentUser != null) {
            com.example.cinemaapp.data.model.User u = com.example.cinemaapp.data.repository.UserRepository.currentUser;
            if (u.name != null) edtName.setText(u.name);
            if (u.phone != null) edtPhone.setText(u.phone);
            if (u.dob != null) edtDob.setText(u.dob);
            if (u.gender != null) spinGender.setText(u.gender, false);
            if (u.favoriteCinema != null) spinCinema.setText(u.favoriteCinema, false);
            if (u.province != null) spinProvince.setText(u.province, false);
            if (u.district != null) spinDistrict.setText(u.district, false);
        }
    }

    private void updateUserInfo() {
        if (com.example.cinemaapp.data.repository.UserRepository.currentUser == null) return;

        String newName = edtName.getText().toString().trim();
        String newPhone = edtPhone.getText().toString().trim();
        String newDob = edtDob.getText() != null ? edtDob.getText().toString().trim() : "";
        String newGender = spinGender.getText().toString().trim();
        String newCinema = spinCinema.getText().toString().trim();
        String newProvince = spinProvince.getText().toString().trim();
        String newDistrict = spinDistrict.getText().toString().trim();

        if (newName.isEmpty() || newPhone.isEmpty()) {
            android.widget.Toast.makeText(this, "Vui lòng nhập đủ tên và số điện thoại", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        com.example.cinemaapp.data.model.User updatedUser = new com.example.cinemaapp.data.model.User();
        updatedUser.id = com.example.cinemaapp.data.repository.UserRepository.currentUser.id;
        updatedUser.name = newName;
        updatedUser.phone = newPhone;
        updatedUser.dob = newDob;
        updatedUser.gender = newGender;
        updatedUser.favoriteCinema = newCinema;
        updatedUser.province = newProvince;
        updatedUser.district = newDistrict;
        
        updatedUser.email = com.example.cinemaapp.data.repository.UserRepository.currentUser.email;
        updatedUser.password = com.example.cinemaapp.data.repository.UserRepository.currentUser.password;

        userRepository.updateUser(updatedUser, new com.example.cinemaapp.data.repository.UserRepository.Callback1<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                runOnUiThread(() -> {
                    android.widget.Toast.makeText(SettingActivity.this, "Cập nhật thành công", android.widget.Toast.LENGTH_SHORT).show();
                    com.example.cinemaapp.data.model.User u = com.example.cinemaapp.data.repository.UserRepository.currentUser;
                    u.name = newName;
                    u.phone = newPhone;
                    u.dob = newDob;
                    u.gender = newGender;
                    u.favoriteCinema = newCinema;
                    u.province = newProvince;
                    u.district = newDistrict;
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> android.widget.Toast.makeText(SettingActivity.this, message, android.widget.Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void changePassword() {
        if (com.example.cinemaapp.data.repository.UserRepository.currentUser == null) return;

        String currentPass = edtCurrentPassword.getText().toString().trim();
        String newPass = edtNewPassword.getText().toString().trim();
        String confirmPass = edtConfirmPassword.getText().toString().trim();

        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            android.widget.Toast.makeText(this, "Vui lòng nhập đủ thông tin mật khẩu", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        if (!currentPass.equals(com.example.cinemaapp.data.repository.UserRepository.currentUser.password)) {
            android.widget.Toast.makeText(this, "Mật khẩu hiện tại không đúng", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            android.widget.Toast.makeText(this, "Mật khẩu xác nhận không khớp", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        com.example.cinemaapp.data.model.User updatedUser = new com.example.cinemaapp.data.model.User();
        updatedUser.id = com.example.cinemaapp.data.repository.UserRepository.currentUser.id;
        updatedUser.password = newPass;

        userRepository.updateUser(updatedUser, new com.example.cinemaapp.data.repository.UserRepository.Callback1<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                runOnUiThread(() -> {
                    android.widget.Toast.makeText(SettingActivity.this, "Đổi mật khẩu thành công, vui lòng đăng nhập lại", android.widget.Toast.LENGTH_SHORT).show();
                    logout();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> android.widget.Toast.makeText(SettingActivity.this, message, android.widget.Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void logout() {
        com.example.cinemaapp.data.repository.UserRepository.currentUser = null;
        android.content.Intent intent = new android.content.Intent(this, com.example.cinemaapp.ui.auth.LoginActivity.class);
        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}