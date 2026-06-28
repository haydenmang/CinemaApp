package com.example.cinemaapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cinemaapp.R;
import com.example.cinemaapp.adapter.CinemaShowtimeAdapter;
import com.example.cinemaapp.adapter.DateSelectorAdapter;
import com.example.cinemaapp.data.api.ApiService;
import com.example.cinemaapp.data.api.SupabaseClient;
import com.example.cinemaapp.data.model.Cinema;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CinemaShowtimeActivity extends AppCompatActivity {

    private static final String TAG = "CinemaShowtime";
    private CinemaShowtimeAdapter adapter;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cinema_showtime);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        RecyclerView rvCinemaList = findViewById(R.id.rvCinemaList);
        adapter = new CinemaShowtimeAdapter(cinema -> {
            Intent intent = new Intent(this, MovieByCinemaActivity.class);
            intent.putExtra("cinema_id", cinema.getId());
            intent.putExtra("cinema_name", cinema.getName());
            intent.putExtra("cinema_address", cinema.getAddress());
            startActivity(intent);
        });
        rvCinemaList.setLayoutManager(new LinearLayoutManager(this));
        rvCinemaList.setAdapter(adapter);

        // Ẩn date selector — không cần ở màn hình chọn rạp
        RecyclerView rvDateSelector = findViewById(R.id.rvDateSelector);
        rvDateSelector.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvDateSelector.setAdapter(new DateSelectorAdapter(buildDateList(), (date, pos) -> {}));

        apiService = SupabaseClient.getClient().create(ApiService.class);
        loadCinemas();
    }

    private List<Calendar> buildDateList() {
        List<Calendar> dates = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            Calendar day = (Calendar) cal.clone();
            day.add(Calendar.DAY_OF_YEAR, i);
            dates.add(day);
        }
        return dates;
    }

    private void loadCinemas() {
        apiService.getCinemas().enqueue(new Callback<List<Cinema>>() {
            @Override
            public void onResponse(Call<List<Cinema>> call, Response<List<Cinema>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> adapter.setData(response.body()));
                } else {
                    Log.e(TAG, "getCinemas failed: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<List<Cinema>> call, Throwable t) {
                Log.e(TAG, "getCinemas error", t);
            }
        });
    }
}
