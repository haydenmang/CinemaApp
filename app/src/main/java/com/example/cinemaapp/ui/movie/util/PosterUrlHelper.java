package com.example.cinemaapp.ui.movie.util;

import android.text.TextUtils;

/**
 * Chuẩn hóa URL poster TMDB (sửa lỗi {@code /t/p//} trong database).
 */
public final class PosterUrlHelper {

    private static final String TMDB_BAD_PATH = "https://image.tmdb.org/t/p//";
    private static final String TMDB_GOOD_PATH = "https://image.tmdb.org/t/p/w500/";

    private PosterUrlHelper() {
    }

    public static String normalize(String posterUrl) {
        if (TextUtils.isEmpty(posterUrl)) {
            return null;
        }
        String url = posterUrl.trim();
        if (url.contains("/t/p//")) {
            url = url.replace("/t/p//", "/t/p/w500/");
        }
        if (url.contains("/t/p/original/")) {
            url = url.replace("/t/p/original/", "/t/p/w500/");
        }
        if (url.contains("/t/pw500/")) {
            url = url.replace("/t/pw500/", "/t/p/w500/");
        }
        if (url.startsWith(TMDB_BAD_PATH)) {
            url = TMDB_GOOD_PATH + url.substring(TMDB_BAD_PATH.length());
        }
        return url;
    }
}
