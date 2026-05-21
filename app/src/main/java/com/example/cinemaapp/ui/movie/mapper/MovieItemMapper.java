package com.example.cinemaapp.ui.movie.mapper;

import com.example.cinemaapp.data.model.Movie;
import com.example.cinemaapp.ui.movie.model.MovieItem;
import com.example.cinemaapp.ui.movie.util.PosterUrlHelper;

/**
 * Chuyển {@link Movie} (Supabase) sang {@link MovieItem} (UI).
 */
public final class MovieItemMapper {

    private static final String DEFAULT_AGE = "P";

    private MovieItemMapper() {
    }

    public static MovieItem fromApiMovie(Movie movie) {
        if (movie == null) {
            return null;
        }
        String ageLimit = movie.getAgeLimit();
        if (ageLimit == null || ageLimit.trim().isEmpty()) {
            ageLimit = DEFAULT_AGE;
        }
        return new MovieItem(
                movie.getId(),
                movie.getTitle(),
                PosterUrlHelper.normalize(movie.getPosterUrl()),
                movie.getDescription(),
                movie.getDuration(),
                movie.getReleaseDate(),
                ageLimit,
                movie.getTrailer(),
                movie.getGenre(),
                movie.getDirector(),
                movie.getCastList(),
                movie.getRating()
        );
    }
}
