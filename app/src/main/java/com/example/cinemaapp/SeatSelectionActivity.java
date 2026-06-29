package com.example.cinemaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SeatSelectionActivity extends AppCompatActivity {

    private LinearLayout seatContainer;
    private TextView tvSelectedSeats, tvTotalPrice;

    private final Set<String> selectedSeats = new HashSet<>();
    private final Set<String> bookedSeats = new HashSet<>();
    private final Set<String> vipRows = new HashSet<>(Arrays.asList("D", "E", "F"));

    private static final String[] ROWS = {"A", "B", "C", "D", "E", "F", "G", "H"};
    private static final int COLS = 10;

    private long seatPrice = 80000; // giá mặc định, nhận từ intent, nhưng sẽ bị ghi đè bởi VIP logic nếu cần
    private long vipPrice = 120000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        seatContainer = findViewById(R.id.seatContainer);
        tvSelectedSeats = findViewById(R.id.tvSelectedSeats);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);

        String movieTitle = getIntent().getStringExtra("movie_title");
        String showtime = getIntent().getStringExtra("showtime");
        seatPrice = getIntent().getLongExtra("seat_price", 85000);
        vipPrice = seatPrice + 35000;

        if (movieTitle != null) ((TextView) findViewById(R.id.tvMovieTitle)).setText(movieTitle);
        if (showtime != null) {
            try {
                java.text.SimpleDateFormat input = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
                java.text.SimpleDateFormat output = new java.text.SimpleDateFormat("HH:mm - dd/MM", java.util.Locale.getDefault());
                java.util.Date d = input.parse(showtime);
                ((TextView) findViewById(R.id.tvShowtime)).setText(d != null ? output.format(d) : showtime);
            } catch (Exception e) {
                ((TextView) findViewById(R.id.tvShowtime)).setText(showtime);
            }
        }

        // Hiển thị giá vé
        ((TextView) findViewById(R.id.tvShowtime)).append("  •  Thường: " + formatPrice(seatPrice) + "đ, VIP: " + formatPrice(vipPrice) + "đ");

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        buildSeatMap();
        updateBottomBar();

        ((AppCompatButton) findViewById(R.id.btnConfirm)).setOnClickListener(v -> {
            if (selectedSeats.isEmpty()) {
                // Rung nút khi chưa chọn ghế
                v.animate().translationX(-10f).setDuration(60)
                    .withEndAction(() -> v.animate().translationX(10f).setDuration(60)
                    .withEndAction(() -> v.animate().translationX(0f).setDuration(60).start())
                    .start()).start();
                Toast.makeText(this, "Vui lòng chọn ít nhất 1 ghế", Toast.LENGTH_SHORT).show();
                return;
            }

            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100)
                .withEndAction(() -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    List<String> sorted = new ArrayList<>(selectedSeats);
                    Collections.sort(sorted);
                    long total = sorted.stream().mapToLong(s ->
                        vipRows.contains(String.valueOf(s.charAt(0))) ? vipPrice : seatPrice).sum();

                    Intent intent = new Intent(this, ComboActivity.class);
                    intent.putExtra("movie_title", getIntent().getStringExtra("movie_title"));
                    intent.putExtra("showtime", getIntent().getStringExtra("showtime"));
                    intent.putExtra("cinema_name", getIntent().getStringExtra("cinema_name"));
                    intent.putExtra("cinema_address", getIntent().getStringExtra("cinema_address"));
                    intent.putStringArrayListExtra("selected_seats", new ArrayList<>(sorted));
                    intent.putExtra("total_price", total);
                    startActivity(intent);
                }).start();
        });
    }

    private void buildSeatMap() {
        int size = dpToPx(32), margin = dpToPx(4);
        for (String row : ROWS) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(Gravity.CENTER_VERTICAL);
            rowLayout.addView(makeLabel(row));

            for (int col = 1; col <= COLS; col++) {
                if (col == 6) rowLayout.addView(makeSpacer(size));
                rowLayout.addView(makeSeat(row, col, size, margin));
            }
            rowLayout.addView(makeLabel(row));
            seatContainer.addView(rowLayout);

            // Fade in từng hàng theo thứ tự
            int rowIndex = java.util.Arrays.asList(ROWS).indexOf(row);
            rowLayout.setAlpha(0f);
            rowLayout.setTranslationY(20f);
            rowLayout.postDelayed(() ->
                rowLayout.animate().alpha(1f).translationY(0f)
                    .setDuration(250)
                    .setInterpolator(new android.view.animation.DecelerateInterpolator())
                    .start()
            , rowIndex * 60L);
        }
    }

    private TextView makeLabel(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(0xFF888888);
        tv.setTextSize(11);
        tv.setWidth(dpToPx(20));
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

    private View makeSpacer(int size) {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(16), size));
        return v;
    }

    private TextView makeSeat(String row, int col, int size, int margin) {
        String seatId = row + col;
        TextView seat = new TextView(this);
        seat.setText(String.valueOf(col));
        seat.setTextSize(9);
        seat.setGravity(Gravity.CENTER);
        seat.setTextColor(0xFFCCCCCC);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(size, size);
        p.setMargins(margin, margin, margin, margin);
        seat.setLayoutParams(p);

        if (bookedSeats.contains(seatId)) {
            seat.setBackground(getDrawable(R.drawable.seat_booked));
            seat.setEnabled(false);
        } else {
            seat.setBackground(getDrawable(vipRows.contains(row) ? R.drawable.seat_vip : R.drawable.seat_empty));
            seat.setOnClickListener(v -> {
                if (selectedSeats.contains(seatId)) {
                    selectedSeats.remove(seatId);
                    seat.setBackground(getDrawable(vipRows.contains(row) ? R.drawable.seat_vip : R.drawable.seat_empty));
                    // Shrink animation khi bỏ chọn
                    seat.animate().scaleX(0.8f).scaleY(0.8f).setDuration(80)
                        .withEndAction(() -> seat.animate().scaleX(1f).scaleY(1f).setDuration(80).start())
                        .start();
                } else {
                    selectedSeats.add(seatId);
                    seat.setBackground(getDrawable(R.drawable.seat_selected));
                    // Bounce animation khi chọn
                    seat.animate().scaleX(1.3f).scaleY(1.3f).setDuration(120)
                        .withEndAction(() -> seat.animate().scaleX(1f).scaleY(1f).setDuration(120)
                        .setInterpolator(new android.view.animation.OvershootInterpolator())
                        .start()).start();
                }
                updateBottomBar();
            });
        }
        return seat;
    }

    private void updateBottomBar() {
        if (selectedSeats.isEmpty()) {
            tvSelectedSeats.setText("Chưa chọn ghế");
            tvTotalPrice.setText("0 đ");
            return;
        }
        List<String> sorted = new ArrayList<>(selectedSeats);
        Collections.sort(sorted);
        long total = sorted.stream().mapToLong(s ->
            vipRows.contains(String.valueOf(s.charAt(0))) ? vipPrice : seatPrice).sum();
        tvSelectedSeats.setText("Ghế: " + String.join(", ", sorted));
        tvTotalPrice.setText(formatPrice(total) + " đ");
    }

    private String formatPrice(long price) {
        return NumberFormat.getNumberInstance(new Locale("vi", "VN")).format(price);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
