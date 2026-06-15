package com.example.cinemaapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class RoomSelectionActivity extends AppCompatActivity {

    // Định nghĩa các loại phòng
    private static final RoomType[] ROOM_TYPES = {
        new RoomType("🏆 Gold Class", "Phòng giường nằm · Phục vụ nước · Ghế massage",
            200000, "#D4AF37", "#7A5C00"),
        new RoomType("🎬 IMAX / 4DX", "Màn hình siêu lớn · Ghế chuyển động · Âm thanh vòm",
            150000, "#2196F3", "#1A3A5C"),
        new RoomType("✨ Premium 3D", "Ghế recliner · Màn hình 3D · Âm thanh Dolby",
            110000, "#9C27B0", "#3A1A5C"),
        new RoomType("🎞 2D Tiêu chuẩn", "Ghế tiêu chuẩn · Màn hình 2D",
            80000, "#888888", "#2A2A3A"),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_selection);

        String movieTitle = getIntent().getStringExtra("movie_title");
        String showtime = getIntent().getStringExtra("showtime");

        if (movieTitle != null) ((TextView) findViewById(R.id.tvMovieTitle)).setText(movieTitle);
        if (showtime != null) ((TextView) findViewById(R.id.tvShowtime)).setText(showtime);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        buildRoomList(movieTitle, showtime);
    }

    private void buildRoomList(String movieTitle, String showtime) {
        LinearLayout container = findViewById(R.id.roomContainer);
        int margin = dpToPx(12);

        for (RoomType room : ROOM_TYPES) {
            // Card phòng
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackground(getDrawable(R.drawable.room_card_bg));
            card.setPadding(dpToPx(20), dpToPx(16), dpToPx(20), dpToPx(16));

            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.setMargins(0, margin, 0, 0);
            card.setLayoutParams(cardParams);

            // Animation slide up fade cho từng card
            card.setAlpha(0f);
            card.setTranslationY(60f);
            int delay = 100 * (java.util.Arrays.asList(ROOM_TYPES).indexOf(room));
            card.postDelayed(() ->
                card.animate().alpha(1f).translationY(0f)
                    .setDuration(400)
                    .setInterpolator(new android.view.animation.DecelerateInterpolator())
                    .start()
            , delay);

            // Header: tên phòng + giá
            LinearLayout header = new LinearLayout(this);
            header.setOrientation(LinearLayout.HORIZONTAL);
            header.setGravity(Gravity.CENTER_VERTICAL);

            TextView tvName = new TextView(this);
            tvName.setText(room.name);
            tvName.setTextColor(Color.parseColor(room.accentColor));
            tvName.setTextSize(17);
            tvName.setTypeface(null, android.graphics.Typeface.BOLD);
            tvName.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            TextView tvPrice = new TextView(this);
            tvPrice.setText(formatPrice(room.price) + " đ");
            tvPrice.setTextColor(Color.parseColor(room.accentColor));
            tvPrice.setTextSize(16);
            tvPrice.setTypeface(null, android.graphics.Typeface.BOLD);

            header.addView(tvName);
            header.addView(tvPrice);

            // Mô tả
            TextView tvDesc = new TextView(this);
            tvDesc.setText(room.description);
            tvDesc.setTextColor(0xFFAAAAAA);
            tvDesc.setTextSize(13);
            tvDesc.setPadding(0, dpToPx(6), 0, dpToPx(12));

            // Nút chọn
            AppCompatButton btnSelect = new AppCompatButton(this);
            btnSelect.setText("Chọn phòng này");
            btnSelect.setTextColor(Color.parseColor(room.bgColor.equals("#2A2A3A") ? "#FFFFFF" : "#FFFFFF"));
            btnSelect.setTextSize(14);
            btnSelect.setBackground(makeButtonBg(room.accentColor));
            btnSelect.setStateListAnimator(null);

            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(44));
            btnSelect.setLayoutParams(btnParams);

            btnSelect.setOnClickListener(v -> {
                Intent intent = new Intent(this, SeatSelectionActivity.class);
                intent.putExtra("movie_title", movieTitle);
                intent.putExtra("showtime", showtime);
                intent.putExtra("room_type", room.name);
                intent.putExtra("seat_price", room.price);
                startActivity(intent);
            });

            card.addView(header);
            card.addView(tvDesc);
            card.addView(btnSelect);
            container.addView(card);
        }
    }

    private android.graphics.drawable.GradientDrawable makeButtonBg(String color) {
        android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
        bg.setColor(Color.parseColor(color));
        bg.setCornerRadius(dpToPx(10));
        return bg;
    }

    private String formatPrice(long price) {
        return String.format("%,d", price).replace(",", ".");
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    static class RoomType {
        String name, description, accentColor, bgColor;
        long price;
        RoomType(String name, String desc, long price, String accent, String bg) {
            this.name = name; this.description = desc; this.price = price;
            this.accentColor = accent; this.bgColor = bg;
        }
    }
}
