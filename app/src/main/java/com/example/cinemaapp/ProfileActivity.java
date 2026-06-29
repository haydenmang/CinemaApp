package com.example.cinemaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView imgSetting = findViewById(R.id.imgSettings);
        imgSetting.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, SettingActivity.class);
            startActivity(intent);
        });

        ImageView imgBell = findViewById(R.id.imgBell);
        imgBell.setOnClickListener(view -> {
            startActivity(new Intent(ProfileActivity.this, NotificationActivity.class));
        });

        findViewById(R.id.menuMovie).setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, com.example.cinemaapp.ui.ChooseMovieActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.menuCinema).setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, com.example.cinemaapp.ui.CinemaShowtimeActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.menuHome).setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, com.example.cinemaapp.ui.home.MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.menuTheater).setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, com.example.cinemaapp.ui.CinemaShowtimeActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.menuMyTicket).setOnClickListener(view -> {
            startActivity(new Intent(ProfileActivity.this, MyTicketActivity.class));
        });

        findViewById(R.id.btnLogout).setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, com.example.cinemaapp.ui.auth.LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}