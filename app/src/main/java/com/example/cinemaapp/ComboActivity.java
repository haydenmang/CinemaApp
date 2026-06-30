package com.example.cinemaapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cinemaapp.adapter.ComboAdapter;
import com.example.cinemaapp.data.api.ApiService;
import com.example.cinemaapp.data.api.SupabaseClient;
import com.example.cinemaapp.data.model.Combo;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComboActivity extends AppCompatActivity {

    private TextView tvComboTotal;
    private final NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
    private RecyclerView recyclerView;
    private ComboAdapter adapter;
    
    private long currentTotalComboPrice = 0;
    private String currentComboDesc = "";
    private Map<Integer, Integer> selectedCombosMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combo);

        tvComboTotal = findViewById(R.id.tvComboTotal);
        recyclerView = findViewById(R.id.recyclerViewCombos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ComboAdapter(new ArrayList<>(), (totalComboPrice, comboDesc, selectedCombos) -> {
            currentTotalComboPrice = totalComboPrice;
            currentComboDesc = comboDesc;
            selectedCombosMap = selectedCombos;
            tvComboTotal.setText(fmt.format(totalComboPrice) + " đ");
        });
        recyclerView.setAdapter(adapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        ((AppCompatButton) findViewById(R.id.btnContinue)).setOnClickListener(v -> goToPayment());
        findViewById(R.id.btnSkip).setOnClickListener(v -> goToPayment());

        loadCombos();
    }

    private void loadCombos() {
        ApiService apiService = SupabaseClient.getClient().create(ApiService.class);
        apiService.getCombos().enqueue(new Callback<List<Combo>>() {
            @Override
            public void onResponse(Call<List<Combo>> call, Response<List<Combo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setCombos(response.body());
                } else {
                    Toast.makeText(ComboActivity.this, "Không thể tải danh sách Combo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Combo>> call, Throwable t) {
                Toast.makeText(ComboActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToPayment() {
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("movie_title", getIntent().getStringExtra("movie_title"));
        intent.putExtra("showtime", getIntent().getStringExtra("showtime"));
        intent.putExtra("showtime_id", getIntent().getIntExtra("showtime_id", -1));
        intent.putExtra("cinema_name", getIntent().getStringExtra("cinema_name"));
        intent.putExtra("cinema_address", getIntent().getStringExtra("cinema_address"));
        intent.putStringArrayListExtra("selected_seats", getIntent().getStringArrayListExtra("selected_seats"));
        intent.putExtra("seat_total", getIntent().getLongExtra("total_price", 0));
        
        intent.putExtra("combo_total", currentTotalComboPrice);
        intent.putExtra("combo_desc", currentComboDesc);
        
        // Convert map to arrays to pass via intent
        ArrayList<Integer> comboIds = new ArrayList<>();
        ArrayList<Integer> comboQtys = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : selectedCombosMap.entrySet()) {
            comboIds.add(entry.getKey());
            comboQtys.add(entry.getValue());
        }
        intent.putIntegerArrayListExtra("combo_ids", comboIds);
        intent.putIntegerArrayListExtra("combo_qtys", comboQtys);
        
        startActivity(intent);
    }
}
