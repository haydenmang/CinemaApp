package com.example.cinemaapp.ui.booking;

import com.example.cinemaapp.R;

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

import android.widget.Toast;
import com.example.cinemaapp.data.api.ApiService;
import com.example.cinemaapp.data.api.SupabaseClient;
import com.example.cinemaapp.data.model.Booking;
import com.example.cinemaapp.data.model.BookingSeat;
import com.example.cinemaapp.data.model.Payment;
import com.example.cinemaapp.data.model.User;
import com.example.cinemaapp.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;
import android.util.Log;

public class PaymentActivity extends AppCompatActivity {

    private static final long NORMAL_PRICE = 85000;
    private static final long VIP_PRICE    = 120000;
    private static final Set<String> VIP_ROWS =
            new HashSet<>(Arrays.asList("D", "E", "F"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        SessionManager sessionManager = new SessionManager(this);
        User currentUser = sessionManager.getUserSession();
        int userId = currentUser != null ? currentUser.id : -1;
        int showtimeId = getIntent().getIntExtra("showtime_id", -1);

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
            if (userId == -1 || showtimeId == -1) {
                Toast.makeText(this, "Lỗi: Không tìm thấy người dùng hoặc suất chiếu!", Toast.LENGTH_SHORT).show();
                return;
            }

            v.setEnabled(false);
            ((AppCompatButton) v).setText("ĐANG XỬ LÝ...");

            RadioGroup rg = findViewById(R.id.rgPayment);
            String method;
            int checked = rg.getCheckedRadioButtonId();
            if (checked == R.id.rbMomo)       method = "MoMo";
            else if (checked == R.id.rbVnpay) method = "VNPay";
            else                              method = "Chuyển khoản";

            ApiService apiService = SupabaseClient.getClient().create(ApiService.class);
            Booking booking = new Booking();
            booking.userId = userId;
            booking.showtimeId = showtimeId;
            booking.totalPrice = finalTotal;
            booking.status = "SUCCESS";

            apiService.createBookingReturning(booking).enqueue(new Callback<List<Booking>>() {
                @Override
                public void onResponse(Call<List<Booking>> call, Response<List<Booking>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        int bookingId = response.body().get(0).id;
                        
                        // Create BookingSeats
                        for (String seatNumber : finalSeats) {
                            BookingSeat bs = new BookingSeat();
                            bs.bookingId = bookingId;
                            bs.showtimeId = showtimeId;
                            bs.seatNumber = seatNumber;
                            apiService.createBookingSeat(bs).enqueue(new Callback<Void>() {
                                @Override public void onResponse(Call<Void> c, Response<Void> res) {}
                                @Override public void onFailure(Call<Void> c, Throwable t) {}
                            });
                        }
                        
                        // Create BookingCombos
                        ArrayList<Integer> comboIds = getIntent().getIntegerArrayListExtra("combo_ids");
                        ArrayList<Integer> comboQtys = getIntent().getIntegerArrayListExtra("combo_qtys");
                        if (comboIds != null && comboQtys != null) {
                            for (int i = 0; i < comboIds.size(); i++) {
                                com.example.cinemaapp.data.model.BookingCombo bc = new com.example.cinemaapp.data.model.BookingCombo();
                                bc.bookingId = bookingId;
                                bc.comboId = comboIds.get(i);
                                bc.quantity = comboQtys.get(i);
                                apiService.createBookingCombo(bc).enqueue(new Callback<Void>() {
                                    @Override public void onResponse(Call<Void> c, Response<Void> res) {}
                                    @Override public void onFailure(Call<Void> c, Throwable t) {}
                                });
                            }
                        }
                        
                        // Create Payment
                        Payment p = new Payment();
                        p.bookingId = bookingId;
                        p.amount = finalTotal;
                        p.method = method;
                        p.status = "SUCCESS";
                        p.transactionCode = "CGV-" + System.currentTimeMillis();
                        apiService.createPayment(p).enqueue(new Callback<Void>() {
                            @Override public void onResponse(Call<Void> c, Response<Void> res) {}
                            @Override public void onFailure(Call<Void> c, Throwable t) {}
                        });

                        runOnUiThread(() -> {
                            Intent intent = new Intent(PaymentActivity.this, TicketActivity.class);
                            intent.putExtra("movie_title", movieTitle);
                            intent.putExtra("showtime", showtime);
                            intent.putExtra("cinema_name", getIntent().getStringExtra("cinema_name"));
                            intent.putExtra("cinema_address", getIntent().getStringExtra("cinema_address"));
                            intent.putExtra("combo_desc", comboDesc);
                            intent.putStringArrayListExtra("selected_seats", finalSeats);
                            intent.putExtra("total_price", finalTotal);
                            intent.putExtra("payment_method", method);
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(PaymentActivity.this, "Lỗi tạo vé!", Toast.LENGTH_SHORT).show();
                            v.setEnabled(true);
                            ((AppCompatButton) v).setText("XÁC NHẬN THANH TOÁN");
                        });
                    }
                }

                @Override
                public void onFailure(Call<List<Booking>> call, Throwable t) {
                    runOnUiThread(() -> {
                        Toast.makeText(PaymentActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                        v.setEnabled(true);
                        ((AppCompatButton) v).setText("XÁC NHẬN THANH TOÁN");
                    });
                }
            });
        });
    }
}
