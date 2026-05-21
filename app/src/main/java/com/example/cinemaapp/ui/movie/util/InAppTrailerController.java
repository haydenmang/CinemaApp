package com.example.cinemaapp.ui.movie.util;

import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cinemaapp.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

/**
 * Phát trailer YouTube ngay trong màn hình chi tiết phim.
 */
public final class InAppTrailerController {

    private final AppCompatActivity activity;
    private final YouTubePlayerView playerView;

    @Nullable
    private YouTubePlayer youTubePlayer;
    @Nullable
    private String pendingVideoId;

    @Nullable
    private View posterView;
    @Nullable
    private View headerOverlay;
    @Nullable
    private View playButton;

    public InAppTrailerController(@NonNull AppCompatActivity activity,
                                  @NonNull YouTubePlayerView playerView) {
        this.activity = activity;
        this.playerView = playerView;
        activity.getLifecycle().addObserver(playerView);
        playerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer player) {
                youTubePlayer = player;
                if (!TextUtils.isEmpty(pendingVideoId)) {
                    player.loadVideo(pendingVideoId, 0f);
                    pendingVideoId = null;
                }
            }

            @Override
            public void onError(@NonNull YouTubePlayer player,
                                @NonNull PlayerConstants.PlayerError error) {
                resetHeaderUi();
                Toast.makeText(activity, R.string.movie_trailer_embed_blocked, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void play(@Nullable String trailerUrl, @NonNull View poster, @Nullable View overlay,
                     @NonNull View playBtn) {
        String videoId = TrailerUrlHelper.extractYouTubeVideoId(trailerUrl);
        if (TextUtils.isEmpty(videoId)) {
            Toast.makeText(activity, R.string.movie_trailer_unavailable, Toast.LENGTH_SHORT).show();
            return;
        }

        posterView = poster;
        headerOverlay = overlay;
        playButton = playBtn;

        poster.setVisibility(View.GONE);
        if (overlay != null) {
            overlay.setVisibility(View.GONE);
        }
        playBtn.setVisibility(View.GONE);
        playerView.setVisibility(View.VISIBLE);

        if (youTubePlayer != null) {
            youTubePlayer.loadVideo(videoId, 0f);
        } else {
            pendingVideoId = videoId;
        }
    }

    public void resetHeaderUi() {
        playerView.setVisibility(View.GONE);
        if (posterView != null) {
            posterView.setVisibility(View.VISIBLE);
        }
        if (headerOverlay != null) {
            headerOverlay.setVisibility(View.VISIBLE);
        }
        if (playButton != null) {
            playButton.setVisibility(View.VISIBLE);
        }
        if (youTubePlayer != null) {
            youTubePlayer.pause();
        }
    }

    public void release() {
        if (youTubePlayer != null) {
            youTubePlayer.pause();
        }
        playerView.release();
    }
}
