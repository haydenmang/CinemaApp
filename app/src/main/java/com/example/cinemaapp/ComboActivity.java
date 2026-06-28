package com.example.cinemaapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ComboActivity extends AppCompatActivity {

    private static final long PRICE1 = 68000;
    private static final long PRICE2 = 88000;
    private static final long PRICE3 = 168000;

    private int qty1 = 0, qty2 = 0, qty3 = 0;
    private TextView tvQty1, tvQty2, tvQty3, tvComboTotal;
    private final NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combo);

        tvQty1 = findViewById(R.id.tvQty1);
        tvQty2 = findViewById(R.id.tvQty2);
        tvQty3 = findViewById(R.id.tvQty3);
        tvComboTotal = findViewById(R.id.tvComboTotal);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnPlus1).setOnClickListener(v -> { qty1++; update(); });
        findViewById(R.id.btnMinus1).setOnClickListener(v -> { if (qty1 > 0) qty1--; update(); });
        findViewById(R.id.btnPlus2).setOnClickListener(v -> { qty2++; update(); });
        findViewById(R.id.btnMinus2).setOnClickListener(v -> { if (qty2 > 0) qty2--; update(); });
        findViewById(R.id.btnPlus3).setOnClickListener(v -> { qty3++; update(); });
        findViewById(R.id.btnMinus3).setOnClickListener(v -> { if (qty3 > 0) qty3--; update(); });

        update();
        ((AppCompatButton) findViewById(R.id.btnContinue)).setOnClickListener(v -> goToPayment());
        findViewById(R.id.btnSkip).setOnClickListener(v -> goToPayment());
    }

    private void update() {
        tvQty1.setText(String.valueOf(qty1));
        tvQty2.setText(String.valueOf(qty2));
        tvQty3.setText(String.valueOf(qty3));
        long total = qty1 * PRICE1 + qty2 * PRICE2 + qty3 * PRICE3;
        tvComboTotal.setText(fmt.format(total) + " đ");
    }

    private void goToPayment() {
        long comboTotal = qty1 * PRICE1 + qty2 * PRICE2 + qty3 * PRICE3;
        StringBuilder desc = new StringBuilder();
        if (qty1 > 0) desc.append("Combo1 x").append(qty1).append(" ");
        if (qty2 > 0) desc.append("Combo2 x").append(qty2).append(" ");
        if (qty3 > 0) desc.append("Combo3 x").append(qty3);

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("movie_title", getIntent().getStringExtra("movie_title"));
        intent.putExtra("showtime", getIntent().getStringExtra("showtime"));
        intent.putExtra("cinema_name", getIntent().getStringExtra("cinema_name"));
        intent.putExtra("cinema_address", getIntent().getStringExtra("cinema_address"));
        intent.putStringArrayListExtra("selected_seats", getIntent().getStringArrayListExtra("selected_seats"));
        intent.putExtra("seat_total", getIntent().getLongExtra("total_price", 0));
        intent.putExtra("combo_total", comboTotal);
        intent.putExtra("combo_desc", desc.toString().trim());
        startActivity(intent);
    }
}
