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
        window.setStatusBarColor(Color.parseColor("#000000"));
        window.setNavigationBarColor(Color.parseColor("#000000"));
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
                Intent intent = new Intent(ChooseMovieActivity.this, com.example.cinemaapp.ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
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
        progressBar.setVisibility(View.VISIBLE);
        apiService.getMovies("id.asc", 100).enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allMovies = response.body();
                    filterMoviesByDate();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ChooseMovieActivity.this, "Không tải được phim từ máy chủ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ChooseMovieActivity.this, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterMoviesByDate() {
        if (allMovies == null || allMovies.isEmpty()) {
            return;
        }

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

        List<Showtime> accumulatedShowtimes = new ArrayList<>();
        fetchShowtimesPage(0, gteDate, lteDate, accumulatedShowtimes);
    }

    private void fetchShowtimesPage(int offset, String gteDate, String lteDate, List<Showtime> accumulated) {
        int limit = 1000;
        String range = offset + "-" + (offset + limit - 1);
        apiService.getShowtimesByDateRangePaginated(range, gteDate, lteDate).enqueue(new Callback<List<Showtime>>() {
            @Override
            public void onResponse(Call<List<Showtime>> call, Response<List<Showtime>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Showtime> chunk = response.body();
                    accumulated.addAll(chunk);
                    if (chunk.size() == limit) {
                        fetchShowtimesPage(offset + limit, gteDate, lteDate, accumulated);
                    } else {
                        processShowtimes(accumulated);
                    }
                } else {
                    processShowtimes(accumulated);
                }
            }

            @Override
            public void onFailure(Call<List<Showtime>> call, Throwable t) {
                processShowtimes(accumulated);
            }
        });
    }

    private void processShowtimes(List<Showtime> showtimes) {
        progressBar.setVisibility(View.GONE);
        Set<Integer> activeMovieIds = new HashSet<>();
        for (Showtime st : showtimes) {
            activeMovieIds.add(st.getMovieId());
        }

        filteredMovies.clear();
        for (Movie m : allMovies) {
            if (activeMovieIds.contains(m.getId())) {
                filteredMovies.add(m);
            }
        }
        updateMovieUi();
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
