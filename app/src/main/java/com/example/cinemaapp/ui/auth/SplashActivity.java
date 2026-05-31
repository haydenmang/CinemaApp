package com.example.cinemaapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinemaapp.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView ivLogo = findViewById(R.id.tvEmoji);
        TextView tvAppName = findViewById(R.id.tvAppName);
        TextView tvTagline = findViewById(R.id.tvTagline);

        // Logo fade in + scale bounce
        ivLogo.animate()
                .alpha(1f)
                .scaleX(1.15f).scaleY(1.15f)
                .setDuration(600)
                .withEndAction(() ->
                        ivLogo.animate().scaleX(1f).scaleY(1f).setDuration(200).start()
                ).start();

        // App name fade in sau 300ms
        tvAppName.postDelayed(() ->
                        tvAppName.animate().alpha(1f).translationYBy(-20f).setDuration(500).start()
                , 300);

        // Tagline fade in sau 600ms
        tvTagline.postDelayed(() ->
                        tvTagline.animate().alpha(1f).translationYBy(-20f).setDuration(500).start()
                , 600);

        // Chuyển sang LoginActivity sau 2.5 giây
        ivLogo.postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }, 2500);
    }
}
