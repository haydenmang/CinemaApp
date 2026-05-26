package com.example.cinemaapp.ui.movie.widget;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class PosterPageTransformer implements ViewPager2.PageTransformer {

    private static final float MIN_SCALE = 0.84f;
    private static final float MIN_ALPHA = 0.5f;

    @Override
    public void transformPage(@NonNull View page, float position) {
        float absPosition = Math.min(Math.abs(position), 1f);

        float scale = MIN_SCALE + (1f - MIN_SCALE) * (1f - absPosition);
        page.setScaleX(scale);
        page.setScaleY(scale);

        page.setAlpha(MIN_ALPHA + (1f - MIN_ALPHA) * (1f - absPosition));

        float translation = -position * page.getWidth() * 0.08f;
        page.setTranslationX(translation);
    }
}
