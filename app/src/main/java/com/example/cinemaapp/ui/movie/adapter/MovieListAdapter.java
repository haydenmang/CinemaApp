package com.example.cinemaapp.ui.movie.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinemaapp.R;
import com.example.cinemaapp.ui.movie.model.MovieItem;
import com.example.cinemaapp.ui.movie.util.PosterImageLoader;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder> {

    private final List<MovieItem> movieList = new ArrayList<>();
    private boolean isComingSoon = false;

    public void setMovies(List<MovieItem> newMovies, boolean isComingSoon) {
        this.isComingSoon = isComingSoon;
        movieList.clear();
        if (newMovies != null) {
            movieList.addAll(newMovies);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        MovieItem movie = movieList.get(position);
        holder.bind(movie, isComingSoon);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgPoster;
        private final TextView txtTitle;
        private final TextView txtAgeLimit;
        private final TextView txtFormat;
        private final TextView txtDuration;
        private final TextView txtGenre;
        private final TextView txtRating;
        private final MaterialButton btnBuyTicket;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgPoster);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtAgeLimit = itemView.findViewById(R.id.txtAgeLimit);
            txtFormat = itemView.findViewById(R.id.txtFormat);
            txtDuration = itemView.findViewById(R.id.txtDuration);
            txtGenre = itemView.findViewById(R.id.txtGenre);
            txtRating = itemView.findViewById(R.id.txtRating);
            btnBuyTicket = itemView.findViewById(R.id.btnBuyTicket);
        }

        public void bind(MovieItem movie, boolean isComingSoon) {
            txtTitle.setText(movie.getTitle());
            txtAgeLimit.setText(movie.getAgeRating() != null && !movie.getAgeRating().isEmpty() ? movie.getAgeRating() : "T13");
            txtFormat.setText("2D");
            txtDuration.setText(movie.getDuration() + " phút");
            txtGenre.setText("Thể loại: " + (movie.getGenre() != null ? movie.getGenre() : "Đang cập nhật"));
            txtRating.setText(movie.getRatingText());

            PosterImageLoader.load(itemView.getContext(), movie.getPosterUrl(), imgPoster);

            if (isComingSoon) {
                btnBuyTicket.setVisibility(View.GONE);
            } else {
                btnBuyTicket.setVisibility(View.VISIBLE);
                btnBuyTicket.setOnClickListener(v -> {
                    android.content.Intent intent = new android.content.Intent(v.getContext(), com.example.cinemaapp.ui.CinemaShowtimeActivity.class);
                    intent.putExtra("MOVIE_ID", movie.getId());
                    v.getContext().startActivity(intent);
                });
            }
        }
    }
}
