package com.example.cinemaapp.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.cinemaapp.R;
import com.example.cinemaapp.data.repository.MovieCatalogRepository;
import com.example.cinemaapp.ui.CinemaShowtimeActivity;
import com.example.cinemaapp.ui.movie.adapter.BannerAdapter;
import com.example.cinemaapp.ui.movie.adapter.MoviePagerAdapter;
import com.example.cinemaapp.ui.movie.model.MovieItem;
import com.example.cinemaapp.ui.movie.widget.PosterPageTransformer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Màn hình chính — danh sách phim từ Supabase.
 */
public class MainActivity extends AppCompatActivity {

    private static final long SLIDE_DELAY = 4000L;
    private static final long BANNER_DELAY = 3000L;

    private final MovieCatalogRepository movieRepository = new MovieCatalogRepository();
    private final List<MovieItem> currentShowingList = new ArrayList<>();
    private final List<Integer> listBanners = new ArrayList<>();

    private ViewPager2 viewPagerMovies;
    private MoviePagerAdapter movieAdapter;
    private ViewPager2 viewPagerBanner;
    private BannerAdapter bannerAdapter;

    private final Handler autoScrollHandler = new Handler(Looper.getMainLooper());
    private Runnable autoScrollRunnable;
    private final Handler bannerHandler = new Handler(Looper.getMainLooper());
    private Runnable bannerRunnable;

    private DrawerLayout drawerLayout;
    private View mainContentHolder;
    private TextView tabDangChieu;
    private TextView tabSapChieu;
    private View tabIndicatorTrack;
    private View tabIndicatorLine;
    private int tabIndicatorWidth;
    private TextView tvSelectedTitle;
    private TextView tvSelectedInfo;
    private BottomNavigationView bottomNavigationView;

    private boolean isDangChieuSelected = true;
    private boolean isSwitchingTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupSystemBars();
        setContentView(R.layout.activity_main);
        bindViews();
        setupWindowInsets();
        setupBannerCarousel();
        setupMovieCarousel();
        setupBottomNavigation();
        setupDrawer();
        setupTabs();
        setupBookButton();
        loadMoviesFromDatabase();
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
        drawerLayout = findViewById(R.id.drawer_layout);
        mainContentHolder = findViewById(R.id.main_content_holder);
        tabDangChieu = findViewById(R.id.tabDangChieu);
        tabSapChieu = findViewById(R.id.tabSapChieu);
        tabIndicatorTrack = findViewById(R.id.tabIndicatorTrack);
        tabIndicatorLine = findViewById(R.id.tabIndicatorLine);
        viewPagerMovies = findViewById(R.id.viewPagerMovies);
        viewPagerBanner = findViewById(R.id.viewPagerBanner);
        tvSelectedTitle = findViewById(R.id.tvSelectedTitle);
        tvSelectedInfo = findViewById(R.id.tvSelectedInfo);
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

    private void setupBannerCarousel() {
        listBanners.clear();
        listBanners.add(R.drawable.banner1);
        listBanners.add(R.drawable.banner2);
        listBanners.add(R.drawable.banner3);
        listBanners.add(R.drawable.banner4);

        bannerAdapter = new BannerAdapter(listBanners);
        viewPagerBanner.setAdapter(bannerAdapter);
        setInitialBannerPosition();

        viewPagerBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                bannerHandler.removeCallbacks(bannerRunnable);
                bannerHandler.postDelayed(bannerRunnable, BANNER_DELAY);
            }
        });

        bannerRunnable = () -> {
            if (listBanners.isEmpty()) {
                return;
            }
            viewPagerBanner.setCurrentItem(viewPagerBanner.getCurrentItem() + 1, true);
        };
    }

    private void setupMovieCarousel() {
        movieAdapter = new MoviePagerAdapter(currentShowingList);
        viewPagerMovies.setAdapter(movieAdapter);
        viewPagerMovies.setOffscreenPageLimit(3);
        viewPagerMovies.setClipToPadding(false);
        viewPagerMovies.setClipChildren(false);

        float density = getResources().getDisplayMetrics().density;
        int sidePadding = (int) (52 * density);
        viewPagerMovies.setPadding(sidePadding, viewPagerMovies.getPaddingTop(),
                sidePadding, viewPagerMovies.getPaddingBottom());

        RecyclerView recyclerView = (RecyclerView) viewPagerMovies.getChildAt(0);
        if (recyclerView != null) {
            recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
            recyclerView.setItemViewCacheSize(6);
        }

        viewPagerMovies.setPageTransformer(new PosterPageTransformer());

        viewPagerMovies.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (currentShowingList.isEmpty() || isSwitchingTab) {
                    return;
                }
                updateSelectedMovieUi(movieAdapter.getMovieAt(position));
                autoScrollHandler.removeCallbacks(autoScrollRunnable);
                autoScrollHandler.postDelayed(autoScrollRunnable, SLIDE_DELAY);
            }
        });

        autoScrollRunnable = () -> {
            if (currentShowingList.isEmpty()) {
                return;
            }
            viewPagerMovies.setCurrentItem(viewPagerMovies.getCurrentItem() + 1, true);
        };
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                resumeAutoScrolls();
                return true;
            }
            pauseAutoScrolls();
            if (id == R.id.nav_movies) {
                android.content.Intent intent = new android.content.Intent(MainActivity.this, com.example.cinemaapp.ui.ChooseMovieActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_theaters) {
                android.content.Intent intent = new android.content.Intent(MainActivity.this, CinemaShowtimeActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_profile) {
                android.content.Intent intent = new android.content.Intent(MainActivity.this, com.example.cinemaapp.ProfileActivity.class);
                startActivity(intent);
            }
            return true;
        });
    }

    private void setupDrawer() {
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                if (mainContentHolder != null) {
                    mainContentHolder.setTranslationX(-drawerView.getWidth() * slideOffset);
                }
            }

            @Override public void onDrawerOpened(@NonNull View drawerView) {}
            @Override public void onDrawerClosed(@NonNull View drawerView) {}
            @Override public void onDrawerStateChanged(int newState) {}
        });
    }

    private void setupTabs() {
        tabIndicatorTrack.post(this::initTabIndicator);

        tabDangChieu.setOnClickListener(v -> {
            if (!isDangChieuSelected) {
                switchMovieTab(movieRepository.getNowShowing(), true);
            }
        });
        tabSapChieu.setOnClickListener(v -> {
            if (isDangChieuSelected) {
                switchMovieTab(movieRepository.getComingSoon(), false);
            }
        });
    }

    private void initTabIndicator() {
        int trackWidth = tabIndicatorTrack.getWidth();
        if (trackWidth <= 0) {
            return;
        }
        tabIndicatorWidth = trackWidth / 2;
        ViewGroup.LayoutParams params = tabIndicatorLine.getLayoutParams();
        params.width = tabIndicatorWidth;
        tabIndicatorLine.setLayoutParams(params);
        moveTabIndicator(isDangChieuSelected, false);
    }

    private void moveTabIndicator(boolean isDangChieu, boolean animate) {
        float targetX = isDangChieu ? 0f : tabIndicatorWidth;
        if (animate) {
            tabIndicatorLine.animate()
                    .translationX(targetX)
                    .setDuration(280)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        } else {
            tabIndicatorLine.setTranslationX(targetX);
        }
    }

    private void setupBookButton() {
        MaterialButton btnBookNow = findViewById(R.id.btnBookNow);
        if (btnBookNow == null) {
            return;
        }
        btnBookNow.setOnClickListener(v -> {
            // TODO: logic đặt vé
            // 1. Kiểm tra xem danh sách phim có bị rỗng không
            if (movieAdapter == null || movieAdapter.getItemCount() == 0) return;
            // 2. Lấy vị trí của bộ phim đang nằm chính giữa màn hình
            int currentPosition = viewPagerMovies.getCurrentItem();
            MovieItem selectedMovie = movieAdapter.getMovieAt(currentPosition);
            if (selectedMovie == null) return;
            // 3. Tạo Intent để chuyển sang trang Chọn Rạp (CinemaShowtimeActivity)
            android.content.Intent intent = new android.content.Intent(MainActivity.this, CinemaShowtimeActivity.class);
            // 4. Gói thông tin của bộ phim đang chọn để gửi sang màn hình Rạp
            // (Màn hình rạp sẽ dựa vào ID phim này để chỉ hiển thị các rạp có chiếu phim này)
            intent.putExtra("MOVIE_ID", selectedMovie.getId());
            intent.putExtra("MOVIE_TITLE", selectedMovie.getTitle());

            // 5. Khởi chạy chuyển trang!
            startActivity(intent);
            });
    }

    private void loadMoviesFromDatabase() {
        movieRepository.loadFromApi(new MovieCatalogRepository.LoadCallback() {
            @Override
            public void onLoaded(List<MovieItem> nowShowing, List<MovieItem> comingSoon, int totalCount) {
                runOnUiThread(MainActivity.this::showMoviesForCurrentTab);
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    showMoviesForCurrentTab();
                    Toast.makeText(MainActivity.this,
                            message != null ? message : "Không tải được phim từ máy chủ",
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showMoviesForCurrentTab() {
        if (!movieRepository.hasData()) {
            return;
        }
        List<MovieItem> target = isDangChieuSelected
                ? movieRepository.getNowShowing()
                : movieRepository.getComingSoon();
        if (target.isEmpty()) {
            return;
        }
        switchMovieTab(target, isDangChieuSelected);
    }

    private void switchMovieTab(List<MovieItem> targetList, boolean isDangChieu) {
        if (targetList == null || targetList.isEmpty()) {
            return;
        }

        isSwitchingTab = true;
        isDangChieuSelected = isDangChieu;
        autoScrollHandler.removeCallbacks(autoScrollRunnable);

        currentShowingList.clear();
        currentShowingList.addAll(targetList);
        movieAdapter.updateMovies(targetList);

        jumpToMiddlePosition(false);
        updateSelectedMovieUi(targetList.get(0));
        updateTabStyles(isDangChieu);

        viewPagerMovies.post(() -> {
            isSwitchingTab = false;
            autoScrollHandler.postDelayed(autoScrollRunnable, SLIDE_DELAY);
        });
    }

    private void jumpToMiddlePosition(boolean smooth) {
        if (movieAdapter.getItemCount() == 0) {
            return;
        }
        viewPagerMovies.setCurrentItem(movieAdapter.getMiddlePosition(), smooth);
    }

    private void updateSelectedMovieUi(MovieItem movie) {
        if (movie == null) {
            return;
        }
        tvSelectedTitle.setText(movie.getTitle());
        tvSelectedInfo.setText(
                getString(R.string.movie_rating_star, movie.getRatingText())
                        + "  •  " + movie.getDuration() + " phút  •  Khởi chiếu: "
                        + movie.getReleaseDate() + "  •  " + movie.getAgeRating()
        );
    }

    private void updateTabStyles(boolean isDangChieu) {
        if (isDangChieu) {
            tabDangChieu.setTextColor(Color.WHITE);
            tabDangChieu.setTypeface(null, android.graphics.Typeface.BOLD);
            tabSapChieu.setTextColor(Color.parseColor("#8A95A5"));
            tabSapChieu.setTypeface(null, android.graphics.Typeface.NORMAL);
            moveTabIndicator(true, true);
        } else {
            tabDangChieu.setTextColor(Color.parseColor("#8A95A5"));
            tabDangChieu.setTypeface(null, android.graphics.Typeface.NORMAL);
            tabSapChieu.setTextColor(Color.WHITE);
            tabSapChieu.setTypeface(null, android.graphics.Typeface.BOLD);
            moveTabIndicator(false, true);
        }
    }

    private void setInitialBannerPosition() {
        if (listBanners.isEmpty()) {
            return;
        }
        int halfMax = Integer.MAX_VALUE / 2;
        int startPosition = halfMax - (halfMax % listBanners.size());
        viewPagerBanner.setCurrentItem(startPosition, false);
    }

    private void resumeAutoScrolls() {
        autoScrollHandler.removeCallbacks(autoScrollRunnable);
        bannerHandler.removeCallbacks(bannerRunnable);
        autoScrollHandler.postDelayed(autoScrollRunnable, SLIDE_DELAY);
        bannerHandler.postDelayed(bannerRunnable, BANNER_DELAY);
    }

    private void pauseAutoScrolls() {
        autoScrollHandler.removeCallbacks(autoScrollRunnable);
        bannerHandler.removeCallbacks(bannerRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseAutoScrolls();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigationView != null
                && bottomNavigationView.getSelectedItemId() == R.id.nav_home) {
            resumeAutoScrolls();
        }
    }
}