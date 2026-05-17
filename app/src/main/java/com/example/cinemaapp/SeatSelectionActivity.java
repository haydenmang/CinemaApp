package com.example.cinemaapp;

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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SeatSelectionActivity extends AppCompatActivity {

    private LinearLayout seatContainer;
    private TextView tvSelectedSeats, tvTotalPrice;
    private AppCompatButton btnConfirm;

    private final Set<String> selectedSeats = new HashSet<>();
    private final Set<String> bookedSeats = new HashSet<>();

    // Hàng VIP
    private final Set<String> vipRows = new HashSet<>(Arrays.asList("D", "E", "F"));

    private static final int COLS = 10;
    private static final String[] ROWS = {"A", "B", "C", "D", "E", "F", "G", "H"};
    private static final long NORMAL_PRICE = 85000;
    private static final long VIP_PRICE = 120000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        seatContainer = findViewById(R.id.seatContainer);
        tvSelectedSeats = findViewById(R.id.tvSelectedSeats);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnConfirm = findViewById(R.id.btnConfirm);

        // Nhận dữ liệu từ intent
        String movieTitle = getIntent().getStringExtra("movie_title");
        String showtime = getIntent().getStringExtra("showtime");
        if (movieTitle != null) ((TextView) findViewById(R.id.tvMovieTitle)).setText(movieTitle);
        if (showtime != null) ((TextView) findViewById(R.id.tvShowtime)).setText(showtime);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        buildSeatMap();
        updateBottomBar();

        btnConfirm.setOnClickListener(v -> {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất 1 ghế", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Đặt vé: " + selectedSeats, Toast.LENGTH_SHORT).show();
        });
    }

    private void buildSeatMap() {
        int seatSize = dpToPx(32);
        int seatMargin = dpToPx(4);

        for (String row : ROWS) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(Gravity.CENTER_VERTICAL);

            // Label hàng
            TextView rowLabel = new TextView(this);
            rowLabel.setText(row);
            rowLabel.setTextColor(0xFF888888);
            rowLabel.setTextSize(12);
            rowLabel.setWidth(dpToPx(20));
            rowLabel.setGravity(Gravity.CENTER);
            rowLayout.addView(rowLabel);

            for (int col = 1; col <= COLS; col++) {
                // Khoảng cách giữa ghế (lối đi giữa)
                if (col == 6) {
                    View space = new View(this);
                    LinearLayout.LayoutParams spaceParams = new LinearLayout.LayoutParams(dpToPx(16), seatSize);
                    space.setLayoutParams(spaceParams);
                    rowLayout.addView(space);
                }

                String seatId = row + col;
                TextView seat = new TextView(this);
                seat.setText(String.valueOf(col));
                seat.setTextSize(9);
                seat.setGravity(Gravity.CENTER);
                seat.setTextColor(0xFFCCCCCC);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(seatSize, seatSize);
                params.setMargins(seatMargin, seatMargin, seatMargin, seatMargin);
                seat.setLayoutParams(params);

                // Set trạng thái ghế
                if (bookedSeats.contains(seatId)) {
                    seat.setBackground(getDrawable(R.drawable.seat_booked));
                    seat.setEnabled(false);
                } else if (vipRows.contains(row)) {
                    seat.setBackground(getDrawable(R.drawable.seat_vip));
                    seat.setTag("vip");
                } else {
                    seat.setBackground(getDrawable(R.drawable.seat_empty));
                }

                seat.setOnClickListener(v -> {
                    if (selectedSeats.contains(seatId)) {
                        selectedSeats.remove(seatId);
                        seat.setBackground(getDrawable(
                            vipRows.contains(row) ? R.drawable.seat_vip : R.drawable.seat_empty));
                    } else {
                        selectedSeats.add(seatId);
                        seat.setBackground(getDrawable(R.drawable.seat_selected));
                    }
                    updateBottomBar();
                });

                rowLayout.addView(seat);
            }

            // Label hàng bên phải
            TextView rowLabelRight = new TextView(this);
            rowLabelRight.setText(row);
            rowLabelRight.setTextColor(0xFF888888);
            rowLabelRight.setTextSize(12);
            rowLabelRight.setWidth(dpToPx(20));
            rowLabelRight.setGravity(Gravity.CENTER);
            rowLayout.addView(rowLabelRight);

            seatContainer.addView(rowLayout);
        }
    }

    private void updateBottomBar() {
        if (selectedSeats.isEmpty()) {
            tvSelectedSeats.setText("Chưa chọn ghế");
            tvTotalPrice.setText("0 đ");
            return;
        }

        // Tính tổng tiền
        long total = 0;
        List<String> sortedSeats = new ArrayList<>(selectedSeats);
        java.util.Collections.sort(sortedSeats);

        for (String seat : sortedSeats) {
            String row = String.valueOf(seat.charAt(0));
            total += vipRows.contains(row) ? VIP_PRICE : NORMAL_PRICE;
        }

        tvSelectedSeats.setText("Ghế: " + String.join(", ", sortedSeats));
        tvTotalPrice.setText(NumberFormat.getNumberInstance(new Locale("vi", "VN"))
            .format(total) + " đ");
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
