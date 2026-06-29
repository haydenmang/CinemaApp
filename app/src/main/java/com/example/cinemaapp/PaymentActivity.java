package com.example.cinemaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class PaymentActivity extends AppCompatActivity {

    private static final long NORMAL_PRICE = 85000;
    private static final long VIP_PRICE    = 120000;
    private static final Set<String> VIP_ROWS =
            new HashSet<>(Arrays.asList("D", "E", "F"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        ArrayList<String> seats = getIntent().getStringArrayListExtra("selected_seats");
        String movieTitle   = getIntent().getStringExtra("movie_title");
        String showtime     = getIntent().getStringExtra("showtime");
        String comboDesc    = getIntent().getStringExtra("combo_desc");
        long comboTotal     = getIntent().getLongExtra("combo_total", 0);
        if (seats == null) seats = new ArrayList<>();

        long seatTotal = getIntent().getLongExtra("seat_total", 0);
        if (seatTotal == 0) {
            for (String seat : seats) {
                String row = String.valueOf(seat.charAt(0));
                seatTotal += VIP_ROWS.contains(row) ? VIP_PRICE : NORMAL_PRICE;
            }
        }
        long total = seatTotal + comboTotal;

        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        ((TextView) findViewById(R.id.tvMovieTitle)).setText(movieTitle != null ? movieTitle : "");
        if (showtime != null) {
            try {
                java.text.SimpleDateFormat input = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                java.text.SimpleDateFormat output = new java.text.SimpleDateFormat("HH:mm - dd/MM", Locale.getDefault());
                java.util.Date d = input.parse(showtime);
                ((TextView) findViewById(R.id.tvShowtime)).setText(d != null ? output.format(d) : showtime);
            } catch (Exception e) {
                ((TextView) findViewById(R.id.tvShowtime)).setText(showtime);
            }
        } else {
            ((TextView) findViewById(R.id.tvShowtime)).setText("");
        }
        ((TextView) findViewById(R.id.tvSeats)).setText(String.join(", ", seats));
        ((TextView) findViewById(R.id.tvSeatCount)).setText(seats.size() + " ghế");
        ((TextView) findViewById(R.id.tvTotalPrice)).setText(fmt.format(total) + " đ");

        // Hiện combo nếu có
        View layoutCombo = findViewById(R.id.layoutCombo);
        if (comboTotal > 0 && comboDesc != null && !comboDesc.isEmpty()) {
            layoutCombo.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.tvComboDesc)).setText(comboDesc);
            ((TextView) findViewById(R.id.tvComboPrice)).setText(fmt.format(comboTotal) + " đ");
        } else {
            layoutCombo.setVisibility(View.GONE);
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        long finalTotal = total;
        ArrayList<String> finalSeats = seats;
        ((AppCompatButton) findViewById(R.id.btnConfirm)).setOnClickListener(v -> {
            RadioGroup rg = findViewById(R.id.rgPayment);
            String method;
            int checked = rg.getCheckedRadioButtonId();
            if (checked == R.id.rbMomo)       method = "MoMo";
            else if (checked == R.id.rbVnpay) method = "VNPay";
            else                              method = "Chuyển khoản";

            Intent intent = new Intent(this, TicketActivity.class);
            intent.putExtra("movie_title", movieTitle);
            intent.putExtra("showtime", showtime);
            intent.putExtra("cinema_name", getIntent().getStringExtra("cinema_name"));
            intent.putExtra("cinema_address", getIntent().getStringExtra("cinema_address"));
            intent.putExtra("combo_desc", comboDesc);
            intent.putStringArrayListExtra("selected_seats", finalSeats);
            intent.putExtra("total_price", finalTotal);
            intent.putExtra("payment_method", method);
            startActivity(intent);
        });
    }
}
