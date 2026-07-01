package com.example.cinemaapp.ui.user;

import com.example.cinemaapp.R;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingActivity extends AppCompatActivity {

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

        android.view.View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        com.example.cinemaapp.utils.SessionManager sessionManager = new com.example.cinemaapp.utils.SessionManager(this);
        com.example.cinemaapp.data.model.User currentUser = sessionManager.getUserSession();
        if (currentUser != null && currentUser.phone != null && !currentUser.phone.isEmpty()) {
            android.widget.EditText edtName = findViewById(R.id.edtName);
            android.widget.EditText edtPhone = findViewById(R.id.edtPhone);

            // Hiển thị tạm thời từ session
            if (edtName != null && currentUser.name != null) {
                edtName.setText(currentUser.name);
            }
            if (edtPhone != null && currentUser.phone != null) {
                edtPhone.setText(currentUser.phone);
            }

            // Gọi DB lấy dữ liệu mới nhất
            com.example.cinemaapp.data.repository.UserRepository userRepository = new com.example.cinemaapp.data.repository.UserRepository();
            userRepository.getUserByPhone(currentUser.phone, new com.example.cinemaapp.data.repository.UserRepository.Callback1<com.example.cinemaapp.data.model.User>() {
                @Override
                public void onResult(com.example.cinemaapp.data.model.User user) {
                    if (user != null) {
                        runOnUiThread(() -> {
                            if (edtName != null && user.name != null) {
                                edtName.setText(user.name);
                            }
                            if (edtPhone != null && user.phone != null) {
                                edtPhone.setText(user.phone);
                            }
                            sessionManager.saveUserSession(user);
                        });
                    }
                }
                @Override
                public void onError(String message) {}
            });
        }
    }
}