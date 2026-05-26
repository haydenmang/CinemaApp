package com.example.cinemaapp.ui.movie.util;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.cinemaapp.R;

public final class PosterImageLoader {

    private PosterImageLoader() {
    }

    public static void load(Context context, @Nullable String posterUrl, ImageView imageView) {
        String url = PosterUrlHelper.normalize(posterUrl);
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.color.bg_poster_surface)
                .error(R.color.bg_poster_surface)
                .transition(DrawableTransitionOptions.withCrossFade(180))
                .into(imageView);
    }

    public static void clear(Context context, ImageView imageView) {
        Glide.with(context).clear(imageView);
    }
}
