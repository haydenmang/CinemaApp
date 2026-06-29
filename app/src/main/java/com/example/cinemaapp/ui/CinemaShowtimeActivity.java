package com.example.cinemaapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cinemaapp.R;
import com.example.cinemaapp.ui.home.MainActivity;
import com.example.cinemaapp.ui.ChooseMovieActivity;
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
    private Calendar selectedDate = Calendar.getInstance(); // Mặc định là hôm nay
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cinema_showtime);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        RecyclerView rvCinemaList = findViewById(R.id.rvCinemaList);
        // Hứng lấy ID phim từ Trang chủ truyền sang (Nếu không có thì mặc định là -1)
        int targetMovieId = getIntent().getIntExtra("MOVIE_ID", -1);
        adapter = new CinemaShowtimeAdapter(cinema -> {
            Intent intent = new Intent(this, MovieByCinemaActivity.class);
            intent.putExtra("cinema_id", cinema.getId());
            intent.putExtra("cinema_name", cinema.getName());
            // Tiếp tục nhét ID phim vào balo để chuyền sang trang Lịch chiếu
            intent.putExtra("MOVIE_ID", targetMovieId);

            intent.putExtra("SELECTED_YEAR", selectedDate.get(Calendar.YEAR));
            intent.putExtra("SELECTED_MONTH", selectedDate.get(Calendar.MONTH));
            intent.putExtra("SELECTED_DAY", selectedDate.get(Calendar.DAY_OF_MONTH));

            startActivity(intent);
        });

        rvCinemaList.setLayoutManager(new LinearLayoutManager(this));
        rvCinemaList.setAdapter(adapter);

        // Khởi tạo Date Selector
        RecyclerView rvDateSelector = findViewById(R.id.rvDateSelector);
        rvDateSelector.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        
        List<Calendar> dates = buildDateList();
        DateSelectorAdapter dateAdapter = new DateSelectorAdapter(dates, (date, pos) -> {
            selectedDate = date;
        });
        
        // Hứng ngày từ màn hình Chọn Phim truyền sang (nếu có)
        int passedYear = getIntent().getIntExtra("SELECTED_YEAR", -1);
        int passedMonth = getIntent().getIntExtra("SELECTED_MONTH", -1);
        int passedDay = getIntent().getIntExtra("SELECTED_DAY", -1);
        
        if (passedYear != -1 && passedMonth != -1 && passedDay != -1) {
            for (int i = 0; i < dates.size(); i++) {
                Calendar d = dates.get(i);
                if (d.get(Calendar.YEAR) == passedYear && 
                    d.get(Calendar.MONTH) == passedMonth && 
                    d.get(Calendar.DAY_OF_MONTH) == passedDay) {
                    selectedDate = d;
                    dateAdapter.setSelectedPosition(i);
                    rvDateSelector.scrollToPosition(i);
                    break;
                }
            }
        }
        
        rvDateSelector.setAdapter(dateAdapter);

        apiService = SupabaseClient.getClient().create(ApiService.class);
        loadCinemas();
        
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_theaters);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Intent intent = new Intent(CinemaShowtimeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_movies) {
                Intent intent = new Intent(CinemaShowtimeActivity.this, ChooseMovieActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_theaters) {
                return true;
            }
            return false;
        });
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
