package com.example.cinemaapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinemaapp.data.repository.MovieCatalogRepository;
import com.example.cinemaapp.ui.movie.adapter.MovieListAdapter;
import com.example.cinemaapp.ui.movie.model.MovieItem;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class MovieListActivity extends AppCompatActivity {

    private MovieListAdapter adapter;
    private final MovieCatalogRepository movieRepository = new MovieCatalogRepository();
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movielist);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        android.widget.ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.recyclerViewMovies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MovieListAdapter();
        recyclerView.setAdapter(adapter);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateMovieList(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        loadMovies();
    }

    private void loadMovies() {
        movieRepository.loadFromApi(new MovieCatalogRepository.LoadCallback() {
            @Override
            public void onLoaded(List<MovieItem> nowShowing, List<MovieItem> comingSoon, int totalCount) {
                runOnUiThread(() -> {
                    updateMovieList(tabLayout.getSelectedTabPosition());
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    android.widget.Toast.makeText(MovieListActivity.this, "Lỗi tải dữ liệu: " + message, android.widget.Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void updateMovieList(int tabPosition) {
        if (!movieRepository.hasData()) return;
        
        List<MovieItem> movies = (tabPosition == 0) 
                ? movieRepository.getNowShowing() 
                : movieRepository.getComingSoon();
        
        adapter.setMovies(movies);
    }
}