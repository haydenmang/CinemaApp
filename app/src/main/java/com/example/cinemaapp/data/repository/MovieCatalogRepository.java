package com.example.cinemaapp.data.repository;

import com.example.cinemaapp.data.api.ApiService;
import com.example.cinemaapp.data.api.SupabaseClient;
import com.example.cinemaapp.data.model.Movie;
import com.example.cinemaapp.ui.movie.mapper.MovieItemMapper;
import com.example.cinemaapp.ui.movie.model.MovieItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Tải phim từ Supabase, sắp theo {@code id} tăng dần:
 * 7 phim đầu → Đang chiếu, 7 phim sau → Sắp chiếu.
 */
public class MovieCatalogRepository {

    public static final int MOVIES_PER_TAB = 7;
    private static final int MOVIE_FETCH_LIMIT = 100;

    public interface LoadCallback {
        void onLoaded(List<MovieItem> nowShowing, List<MovieItem> comingSoon, int totalCount);

        void onError(String message);
    }

    private final List<MovieItem> nowShowing = new ArrayList<>();
    private final List<MovieItem> comingSoon = new ArrayList<>();
    private boolean hasData;

    public List<MovieItem> getNowShowing() {
        return nowShowing;
    }

    public List<MovieItem> getComingSoon() {
        return comingSoon;
    }

    public boolean hasData() {
        return hasData;
    }

    public void loadFromApi(LoadCallback callback) {
        SupabaseClient.getClient()
                .create(ApiService.class)
                .getMovies("id.asc", MOVIE_FETCH_LIMIT)
                .enqueue(new Callback<List<Movie>>() {
                    @Override
                    public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            applyMoviesFromApi(response.body());
                        } else {
                            clearLists();
                        }
                        notifyLoaded(callback);
                    }

                    @Override
                    public void onFailure(Call<List<Movie>> call, Throwable t) {
                        clearLists();
                        if (callback != null) {
                            callback.onError(t.getMessage() != null ? t.getMessage() : "Lỗi tải phim");
                        }
                        notifyLoaded(callback);
                    }
                });
    }

    private void applyMoviesFromApi(List<Movie> movies) {
        List<Movie> sorted = new ArrayList<>(movies);
        Collections.sort(sorted, Comparator.comparingInt(Movie::getId));

        List<MovieItem> all = new ArrayList<>();
        for (Movie movie : sorted) {
            MovieItem item = MovieItemMapper.fromApiMovie(movie);
            if (item != null) {
                all.add(item);
            }
        }
        splitIntoTabs(all);
    }

    static void splitIntoTabs(List<MovieItem> all, List<MovieItem> outNowShowing,
                              List<MovieItem> outComingSoon) {
        outNowShowing.clear();
        outComingSoon.clear();
        if (all == null || all.isEmpty()) {
            return;
        }
        int splitIndex = Math.min(MOVIES_PER_TAB, all.size());
        outNowShowing.addAll(all.subList(0, splitIndex));
        if (splitIndex < all.size()) {
            outComingSoon.addAll(all.subList(splitIndex, all.size()));
        }
    }

    private void splitIntoTabs(List<MovieItem> all) {
        splitIntoTabs(all, nowShowing, comingSoon);
        hasData = !nowShowing.isEmpty() || !comingSoon.isEmpty();
    }

    private void clearLists() {
        nowShowing.clear();
        comingSoon.clear();
        hasData = false;
    }

    private void notifyLoaded(LoadCallback callback) {
        if (callback != null) {
            int total = nowShowing.size() + comingSoon.size();
            callback.onLoaded(nowShowing, comingSoon, total);
        }
    }
}
