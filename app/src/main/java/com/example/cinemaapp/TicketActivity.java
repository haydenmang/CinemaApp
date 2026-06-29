package com.example.cinemaapp;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.example.cinemaapp.ui.home.MainActivity;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class TicketActivity extends AppCompatActivity {

    private String movieTitleSaved, showtimeSaved, bookingCodeSaved, paymentMethodSaved, comboDescSaved, cinemaAddressSaved;
    private ArrayList<String> seatsSaved;
    private long totalPriceSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        movieTitleSaved    = getIntent().getStringExtra("movie_title");
        showtimeSaved      = getIntent().getStringExtra("showtime");
        seatsSaved         = getIntent().getStringArrayListExtra("selected_seats");
        totalPriceSaved    = getIntent().getLongExtra("total_price", 0);
        paymentMethodSaved = getIntent().getStringExtra("payment_method");
        comboDescSaved     = getIntent().getStringExtra("combo_desc");
        cinemaAddressSaved = getIntent().getStringExtra("cinema_address");
        if (seatsSaved == null) seatsSaved = new ArrayList<>();

        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        bookingCodeSaved = "CGV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        ((TextView) findViewById(R.id.tvMovieTitle)).setText(movieTitleSaved != null ? movieTitleSaved : "");
        ((TextView) findViewById(R.id.tvShowtime)).setText(showtimeSaved != null ? showtimeSaved : "");
        ((TextView) findViewById(R.id.tvSeats)).setText(String.join(", ", seatsSaved));
        ((TextView) findViewById(R.id.tvTotalPrice)).setText(fmt.format(totalPriceSaved) + " đ");
        ((TextView) findViewById(R.id.tvPaymentMethod)).setText(paymentMethodSaved != null ? paymentMethodSaved : "");
        ((TextView) findViewById(R.id.tvBookingCode)).setText("Mã đặt vé: " + bookingCodeSaved);

        String qrContent = bookingCodeSaved + "|" + movieTitleSaved + "|" + String.join(",", seatsSaved);
        ((ImageView) findViewById(R.id.ivQrCode)).setImageBitmap(generateQr(qrContent, 400));

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        ((AppCompatButton) findViewById(R.id.btnSave)).setOnClickListener(v -> saveTicketImage());
        ((AppCompatButton) findViewById(R.id.btnHome)).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finishAffinity();
        });
    }

    private void saveTicketImage() {
        View ticketView = getLayoutInflater().inflate(R.layout.layout_ticket_image, null);
        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

        ((TextView) ticketView.findViewById(R.id.imgTvMovieTitle)).setText(movieTitleSaved != null ? movieTitleSaved : "");
        ((TextView) ticketView.findViewById(R.id.imgTvShowtime)).setText(formatShowtime(showtimeSaved));
        ((TextView) ticketView.findViewById(R.id.imgTvSeats)).setText(String.join(", ", seatsSaved));
        ((TextView) ticketView.findViewById(R.id.imgTvTicketCount)).setText(seatsSaved.size() + " vé");
        ((TextView) ticketView.findViewById(R.id.imgTvRoom)).setText("Phòng 1");
        ((TextView) ticketView.findViewById(R.id.imgTvCombo)).setText(
                (comboDescSaved != null && !comboDescSaved.isEmpty()) ? comboDescSaved : "Không có");
        ((TextView) ticketView.findViewById(R.id.imgTvAddress)).setText(
                cinemaAddressSaved != null ? cinemaAddressSaved : "");
        ((TextView) ticketView.findViewById(R.id.imgTvBookingCode)).setText("Mã đặt vé: " + bookingCodeSaved);

        String qrContent = bookingCodeSaved + "|" + movieTitleSaved + "|" + String.join(",", seatsSaved);
        ((ImageView) ticketView.findViewById(R.id.imgIvQrCode)).setImageBitmap(generateQr(qrContent, 360));

        int width = (int) (360 * getResources().getDisplayMetrics().density);
        ticketView.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        ticketView.layout(0, 0, ticketView.getMeasuredWidth(), ticketView.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(ticketView.getMeasuredWidth(), ticketView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        ticketView.draw(canvas);

        try {
            String fileName = "Ve_CGV_" + System.currentTimeMillis() + ".png";
            OutputStream out;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                out = getContentResolver().openOutputStream(uri);
            } else {
                java.io.File file = new java.io.File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName);
                out = new java.io.FileOutputStream(file);
            }
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
            Toast.makeText(this, "Đã lưu ảnh vé vào Thư viện!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi lưu ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String formatShowtime(String raw) {
        if (raw == null) return "";
        try {
            java.text.SimpleDateFormat input  = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            java.text.SimpleDateFormat output = new java.text.SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());
            java.util.Date date = input.parse(raw);
            return date != null ? output.format(date) : raw;
        } catch (Exception e) {
            return raw;
        }
    }

    private Bitmap generateQr(String content, int size) {
        int[] hash = new int[content.length()];
        for (int i = 0; i < content.length(); i++) hash[i] = content.charAt(i);
        int grid = 25, cell = size / grid;
        Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);
        for (int y = 0; y < grid; y++) {
            for (int x = 0; x < grid; x++) {
                boolean isFinder = (x < 7 && y < 7) || (x >= grid - 7 && y < 7) || (x < 7 && y >= grid - 7);
                boolean isData   = ((hash[(x * y + x + y) % hash.length] + x + y) % 3 == 0);
                int color = (isFinder || isData) ? Color.BLACK : Color.WHITE;
                for (int dy = 0; dy < cell; dy++)
                    for (int dx = 0; dx < cell; dx++)
                        if (x * cell + dx < size && y * cell + dy < size)
                            bmp.setPixel(x * cell + dx, y * cell + dy, color);
            }
        }
        return bmp;
    }
}
