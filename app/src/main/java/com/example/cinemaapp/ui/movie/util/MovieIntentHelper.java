package com.example.cinemaapp.ui.movie.util;

import android.content.Context;
import android.content.Intent;

import com.example.cinemaapp.ui.movie.detail.MovieDetailActivity;
import com.example.cinemaapp.ui.movie.model.MovieItem;

public final class MovieIntentHelper {

    public static final String EXTRA_MOVIE = "extra_movie";

    private MovieIntentHelper() {
    }

    public static Intent newDetailIntent(Context context, MovieItem movie) {
        Intent intent = new Intent(context, MovieDetailActivity.class);
        intent.putExtra(EXTRA_MOVIE, movie);
        return intent;
    }

    public static MovieItem getMovie(Intent intent) {
        if (intent == null) {
            return null;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            return intent.getParcelableExtra(EXTRA_MOVIE, MovieItem.class);
        }
        return intent.getParcelableExtra(EXTRA_MOVIE);
    }
}
