package com.example.cinemaapp;

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
        if (currentUser != null) {
            android.widget.EditText edtName = findViewById(R.id.edtName);
            if (edtName != null && currentUser.name != null) {
                edtName.setText(currentUser.name);
            }
            android.widget.EditText edtPhone = findViewById(R.id.edtPhone);
            if (edtPhone != null && currentUser.phone != null) {
                edtPhone.setText(currentUser.phone);
            }
        }
    }
}