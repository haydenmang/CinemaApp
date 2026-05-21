package com.example.cinemaapp.ui.movie.util;

import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lấy ID video YouTube từ link trailer (YouTube hoặc TMDB {@code #play=}).
 */
public final class TrailerUrlHelper {

    private static final Pattern TMDB_PLAY_ID = Pattern.compile("#play=([\\w-]+)");
    private static final Pattern YOUTUBE_ID_PARAM = Pattern.compile("[?&]v=([\\w-]+)");

    private TrailerUrlHelper() {
    }

    @Nullable
    public static String toWatchUrl(@Nullable String trailerUrl) {
        String videoId = extractYouTubeVideoId(trailerUrl);
        if (TextUtils.isEmpty(videoId)) {
            return null;
        }
        return "https://www.youtube.com/watch?v=" + videoId;
    }

    @Nullable
    public static String extractYouTubeVideoId(@Nullable String trailerUrl) {
        if (TextUtils.isEmpty(trailerUrl)) {
            return null;
        }
        String url = trailerUrl.trim();

        Matcher tmdbPlay = TMDB_PLAY_ID.matcher(url);
        if (tmdbPlay.find()) {
            return tmdbPlay.group(1);
        }

        if (url.contains("youtu.be/")) {
            Uri uri = Uri.parse(url);
            String id = uri.getLastPathSegment();
            return sanitizeId(id);
        }

        if (url.contains("youtube.com/embed/")) {
            int index = url.indexOf("youtube.com/embed/");
            String rest = url.substring(index + "youtube.com/embed/".length());
            int end = rest.indexOf('?');
            if (end > 0) {
                rest = rest.substring(0, end);
            }
            return sanitizeId(rest);
        }

        if (url.contains("youtube.com")) {
            Matcher matcher = YOUTUBE_ID_PARAM.matcher(url);
            if (matcher.find()) {
                return sanitizeId(matcher.group(1));
            }
        }

        return null;
    }

    @Nullable
    private static String sanitizeId(@Nullable String id) {
        if (TextUtils.isEmpty(id)) {
            return null;
        }
        int slash = id.indexOf('/');
        if (slash > 0) {
            id = id.substring(0, slash);
        }
        return id.trim();
    }
}
