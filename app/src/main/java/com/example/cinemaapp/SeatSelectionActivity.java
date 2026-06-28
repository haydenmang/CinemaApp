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
    private final Set<String> bookedSeats   = new HashSet<>();
    private final Set<String> vipRows       = new HashSet<>(Arrays.asList("D", "E", "F"));

    private static final int COLS         = 10;
    private static final String[] ROWS    = {"A", "B", "C", "D", "E", "F", "G", "H"};
    private static final long NORMAL_PRICE = 85000;
    private static final long VIP_PRICE    = 120000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        seatContainer   = findViewById(R.id.seatContainer);
        tvSelectedSeats = findViewById(R.id.tvSelectedSeats);
        tvTotalPrice    = findViewById(R.id.tvTotalPrice);

        String movieTitle = getIntent().getStringExtra("movie_title");
        String showtime   = getIntent().getStringExtra("showtime");
        if (movieTitle != null) ((TextView) findViewById(R.id.tvMovieTitle)).setText(movieTitle);
        if (showtime != null)   ((TextView) findViewById(R.id.tvShowtime)).setText(showtime);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        buildSeatMap();
        updateBottomBar();

        ((AppCompatButton) findViewById(R.id.btnConfirm)).setOnClickListener(v -> {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất 1 ghế", Toast.LENGTH_SHORT).show();
                return;
            }
            List<String> sorted = new ArrayList<>(selectedSeats);
            Collections.sort(sorted);
            long total = sorted.stream().mapToLong(s ->
                vipRows.contains(String.valueOf(s.charAt(0))) ? VIP_PRICE : NORMAL_PRICE).sum();

            Intent intent = new Intent(this, ComboActivity.class);
            intent.putExtra("movie_title", getIntent().getStringExtra("movie_title"));
            intent.putExtra("showtime", getIntent().getStringExtra("showtime"));
            intent.putExtra("cinema_name", getIntent().getStringExtra("cinema_name"));
            intent.putExtra("cinema_address", getIntent().getStringExtra("cinema_address"));
            intent.putStringArrayListExtra("selected_seats", new ArrayList<>(sorted));
            intent.putExtra("total_price", total);
            startActivity(intent);
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
        }
    }

    private TextView makeLabel(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(0xFF888888);
        tv.setTextSize(12);
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
                } else {
                    selectedSeats.add(seatId);
                    seat.setBackground(getDrawable(R.drawable.seat_selected));
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
            vipRows.contains(String.valueOf(s.charAt(0))) ? VIP_PRICE : NORMAL_PRICE).sum();
        tvSelectedSeats.setText("Ghế: " + String.join(", ", sorted));
        tvTotalPrice.setText(NumberFormat.getNumberInstance(new Locale("vi", "VN")).format(total) + " đ");
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
