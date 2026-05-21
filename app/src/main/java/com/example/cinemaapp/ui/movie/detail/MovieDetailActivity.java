package com.example.cinemaapp.ui.movie.detail;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinemaapp.R;
import com.example.cinemaapp.ui.movie.model.MovieItem;
import com.example.cinemaapp.ui.movie.util.InAppTrailerController;
import com.example.cinemaapp.ui.movie.util.MovieIntentHelper;
import com.example.cinemaapp.ui.movie.util.PosterImageLoader;
import com.google.android.material.button.MaterialButton;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class MovieDetailActivity extends AppCompatActivity {

    private static final int COLLAPSED_MAX_LINES = 4;

    private TextView tvDetailDesc;
    private TextView tvToggleDesc;
    private boolean isExpanded;

    private InAppTrailerController trailerController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        MovieItem movie = MovieIntentHelper.getMovie(getIntent());
        if (movie == null) {
            Toast.makeText(this, "Không tìm thấy thông tin phim", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        YouTubePlayerView youtubePlayerView = findViewById(R.id.youtubePlayerView);
        trailerController = new InAppTrailerController(this, youtubePlayerView);

        bindViews(movie);
    }

    @Override
    protected void onDestroy() {
        if (trailerController != null) {
            trailerController.release();
        }
        super.onDestroy();
    }

    private void bindViews(MovieItem movie) {
        ImageView imgDetailHeaderBackground = findViewById(R.id.imgDetailHeaderBackground);
        View vHeaderOverlay = findViewById(R.id.vHeaderOverlay);
        ImageButton imgPlayTrailer = findViewById(R.id.imgPlayTrailer);
        ImageButton btnBack = findViewById(R.id.btnBack);

        ImageView imgDetailSmallPoster = findViewById(R.id.imgDetailSmallPoster);
        TextView tvDetailAgeBadge = findViewById(R.id.tvDetailAgeBadge);
        TextView tvDetailTitle = findViewById(R.id.tvDetailTitle);
        TextView tvDetailInfo = findViewById(R.id.tvDetailInfo);
        tvDetailDesc = findViewById(R.id.tvDetailDesc);
        tvToggleDesc = findViewById(R.id.tvToggleDesc);
        MaterialButton btnBookNowDetail = findViewById(R.id.btnBookNowDetail);

        btnBack.setOnClickListener(v -> finish());
        btnBookNowDetail.setOnClickListener(v -> {
            // TODO: logic đặt vé
        });

        tvDetailTitle.setText(movie.getTitle());
        setupAgeBadge(tvDetailAgeBadge, movie.getAgeRating());
        tvDetailInfo.setText(
                getString(R.string.movie_rating_star, movie.getRatingText())
                        + "  •  " + movie.getDuration() + " phút  •  Khởi chiếu: "
                        + movie.getReleaseDate()
        );

        String description = TextUtils.isEmpty(movie.getDescription())
                ? getString(R.string.movie_detail_placeholder)
                : movie.getDescription();
        tvDetailDesc.setText(description);
        setupDescriptionToggle();

        bindMetaRow(R.id.rowRating, getString(R.string.movie_detail_rating), movie.getRatingText());
        bindMetaRow(R.id.rowGenre, getString(R.string.movie_detail_genre), movie.getGenre());
        bindMetaRow(R.id.rowDirector, getString(R.string.movie_detail_director), movie.getDirector());
        bindMetaRow(R.id.rowCast, getString(R.string.movie_detail_cast), movie.getCast());

        PosterImageLoader.load(this, movie.getPosterUrl(), imgDetailHeaderBackground);
        PosterImageLoader.load(this, movie.getPosterUrl(), imgDetailSmallPoster);

        imgPlayTrailer.setOnClickListener(v ->
                trailerController.play(
                        movie.getTrailerUrl(),
                        imgDetailHeaderBackground,
                        vHeaderOverlay,
                        imgPlayTrailer
                )
        );
    }

    private void setupAgeBadge(TextView tvAgeBadge, String ageRating) {
        if (tvAgeBadge == null) return;
        if (TextUtils.isEmpty(ageRating)) {
            ageRating = "P";
        }
        tvAgeBadge.setText(ageRating);

        int color;
        switch (ageRating.toUpperCase().trim()) {
            case "P":
                color = Color.parseColor("#2ECC71");
                break;
            case "T13":
                color = Color.parseColor("#3498DB");
                break;
            case "T16":
                color = Color.parseColor("#E67E22");
                break;
            case "T18":
                color = Color.parseColor("#E74C3C");
                break;
            default:
                color = Color.parseColor("#95A5A6");
                break;
        }
        if (tvAgeBadge.getBackground() != null) {
            tvAgeBadge.getBackground().setTint(color);
        }
    }

    private void bindMetaRow(int rowIncludeId, String label, String value) {
        View row = findViewById(rowIncludeId);
        TextView tvLabel = row.findViewById(R.id.tvMetaLabel);
        TextView tvValue = row.findViewById(R.id.tvMetaValue);
        tvLabel.setText(label);
        tvValue.setText(TextUtils.isEmpty(value) ? getString(R.string.movie_detail_placeholder) : value);
    }

    private void setupDescriptionToggle() {
        tvToggleDesc.setOnClickListener(v -> {
            if (isExpanded) {
                tvDetailDesc.setMaxLines(COLLAPSED_MAX_LINES);
                tvToggleDesc.setText(R.string.movie_detail_read_more);
            } else {
                tvDetailDesc.setMaxLines(Integer.MAX_VALUE);
                tvToggleDesc.setText(R.string.movie_detail_read_less);
            }
            isExpanded = !isExpanded;
        });

        tvDetailDesc.post(() -> {
            tvDetailDesc.setMaxLines(Integer.MAX_VALUE);
            tvDetailDesc.post(() -> {
                boolean needsToggle = tvDetailDesc.getLineCount() > COLLAPSED_MAX_LINES;
                tvDetailDesc.setMaxLines(COLLAPSED_MAX_LINES);
                tvToggleDesc.setVisibility(needsToggle ? View.VISIBLE : View.GONE);
            });
        });
    }
}
