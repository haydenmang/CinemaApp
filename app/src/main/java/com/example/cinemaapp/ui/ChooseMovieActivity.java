package com.example.cinemaapp.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cinemaapp.R;
import com.example.cinemaapp.adapter.ChooseMovieAdapter;
import com.example.cinemaapp.adapter.DateSelectorAdapter;
import com.example.cinemaapp.data.api.ApiService;
import com.example.cinemaapp.data.api.SupabaseClient;
import com.example.cinemaapp.data.model.Movie;
import com.example.cinemaapp.data.model.Showtime;
import com.example.cinemaapp.ui.home.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseMovieActivity extends AppCompatActivity {

    private RecyclerView rvDateSelector;
    private RecyclerView rvMovieList;
    private View layoutEmpty;
    private ProgressBar progressBar;
    private BottomNavigationView bottomNavigationView;

    private DateSelectorAdapter dateAdapter;
    private ChooseMovieAdapter movieAdapter;
    private ApiService apiService;

    private final List<Calendar> dates = new ArrayList<>();
    private Calendar selectedDate = Calendar.getInstance();

    private List<Movie> allMovies = new ArrayList<>();
    private final List<Movie> filteredMovies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupSystemBars();
        setContentView(R.layout.activity_choose_movie);

        bindViews();
        setupWindowInsets();
        
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        setupBottomNavigation();
        setupDateSelector();
        setupMovieList();

        apiService = SupabaseClient.getClient().create(ApiService.class);
        loadAllMovies();
    }

    private void setupSystemBars() {
        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);
        window.setStatusBarColor(Color.parseColor("#141C38"));
        window.setNavigationBarColor(Color.parseColor("#0C1020"));
        WindowCompat.getInsetsController(window, window.getDecorView())
                .setAppearanceLightStatusBars(false);
    }

    private void bindViews() {
        rvDateSelector = findViewById(R.id.rvDateSelector);
        rvMovieList = findViewById(R.id.rvMovieList);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        progressBar = findViewById(R.id.progressBar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupWindowInsets() {
        View layoutHeader = findViewById(R.id.layoutHeader);
        View mainRootLayout = findViewById(R.id.main_root_layout);
        if (mainRootLayout == null || layoutHeader == null) {
            return;
        }
        final int headerPaddingTop = layoutHeader.getPaddingTop();
        final int headerPaddingBottom = layoutHeader.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(mainRootLayout, (v, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            layoutHeader.setPadding(
                    layoutHeader.getPaddingLeft(),
                    headerPaddingTop + systemBars.top,
                    layoutHeader.getPaddingRight(),
                    headerPaddingBottom
            );
            if (bottomNavigationView != null) {
                bottomNavigationView.setPadding(
                        bottomNavigationView.getPaddingLeft(),
                        bottomNavigationView.getPaddingTop(),
                        bottomNavigationView.getPaddingRight(),
                        systemBars.bottom
                );
            }
            return windowInsets;
        });
        ViewCompat.requestApplyInsets(mainRootLayout);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_movies);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Intent intent = new Intent(ChooseMovieActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_movies) {
                return true;
            } else if (id == R.id.nav_theaters) {
                Intent intent = new Intent(ChooseMovieActivity.this, CinemaShowtimeActivity.class);
                intent.putExtra("MOVIE_ID", -1);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_profile) {
                Toast.makeText(this, "Chuyển đến màn hình Tài Khoản", Toast.LENGTH_SHORT).show();
            }
            return false;
        });
    }

    private void setupDateSelector() {
        dates.clear();
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            Calendar day = (Calendar) cal.clone();
            day.add(Calendar.DAY_OF_YEAR, i);
            dates.add(day);
        }

        rvDateSelector.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        dateAdapter = new DateSelectorAdapter(dates, (date, position) -> {
            selectedDate = date;
            filterMoviesByDate();
        });
        rvDateSelector.setAdapter(dateAdapter);
    }

    private void setupMovieList() {
        rvMovieList.setLayoutManager(new LinearLayoutManager(this));
        movieAdapter = new ChooseMovieAdapter(movie -> {
            Intent intent = new Intent(ChooseMovieActivity.this, CinemaShowtimeActivity.class);
            intent.putExtra("MOVIE_ID", movie.getId());
            intent.putExtra("MOVIE_TITLE", movie.getTitle());
            
            // Pass the selected date so it carries over
            intent.putExtra("SELECTED_YEAR", selectedDate.get(Calendar.YEAR));
            intent.putExtra("SELECTED_MONTH", selectedDate.get(Calendar.MONTH));
            intent.putExtra("SELECTED_DAY", selectedDate.get(Calendar.DAY_OF_MONTH));
            
            startActivity(intent);
        });
        rvMovieList.setAdapter(movieAdapter);
    }

    private void loadAllMovies() {
        filterMoviesByDate();
    }

    private void filterMoviesByDate() {
        progressBar.setVisibility(View.VISIBLE);
        rvMovieList.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);

        // Format dates
        int year = selectedDate.get(Calendar.YEAR);
        int month = selectedDate.get(Calendar.MONTH);
        int day = selectedDate.get(Calendar.DAY_OF_MONTH);
        String yyyyMMdd = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
        String gteDate = "gte." + yyyyMMdd + "T00:00:00";
        String lteDate = "lte." + yyyyMMdd + "T23:59:59";

        apiService.getMoviesPlayingOnDate(gteDate, lteDate).enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    filteredMovies.clear();
                    filteredMovies.addAll(response.body());
                    updateMovieUi();
                } else {
                    filteredMovies.clear();
                    updateMovieUi();
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                filteredMovies.clear();
                updateMovieUi();
                Toast.makeText(ChooseMovieActivity.this, "Lỗi tải phim", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMovieUi() {
        movieAdapter.setData(filteredMovies);
        if (filteredMovies.isEmpty()) {
            rvMovieList.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvMovieList.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_movies);
        }
    }
}
