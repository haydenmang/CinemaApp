package com.example.cinemaapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cinemaapp.R;
import com.example.cinemaapp.data.model.Movie;
import com.example.cinemaapp.ui.movie.util.PosterImageLoader;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class ChooseMovieAdapter extends RecyclerView.Adapter<ChooseMovieAdapter.ViewHolder> {

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    private List<Movie> movies = new ArrayList<>();
    private final OnMovieClickListener listener;

    public ChooseMovieAdapter(OnMovieClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_choose_movie, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.tvMovieTitle.setText(movie.getTitle());
        holder.tvMovieDuration.setText(movie.getDuration() + " phút");
        holder.tvMovieRating.setText("★ " + movie.getRating());
        
        if (movie.getAgeLimit() != null && !movie.getAgeLimit().trim().isEmpty()) {
            holder.tvMovieAgeRating.setVisibility(View.VISIBLE);
            holder.tvMovieAgeRating.setText(movie.getAgeLimit());
        } else {
            holder.tvMovieAgeRating.setVisibility(View.GONE);
        }

        PosterImageLoader.load(holder.itemView.getContext(), movie.getPosterUrl(), holder.ivMoviePoster);

        View.OnClickListener clickListener = v -> {
            if (listener != null) {
                listener.onMovieClick(movie);
            }
        };

        holder.itemView.setOnClickListener(clickListener);
        holder.btnBook.setOnClickListener(clickListener);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMoviePoster;
        TextView tvMovieTitle, tvMovieDuration, tvMovieRating, tvMovieAgeRating;
        MaterialButton btnBook;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMoviePoster = itemView.findViewById(R.id.ivMoviePoster);
            tvMovieTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvMovieDuration = itemView.findViewById(R.id.tvMovieDuration);
            tvMovieRating = itemView.findViewById(R.id.tvMovieRating);
            tvMovieAgeRating = itemView.findViewById(R.id.tvMovieAgeRating);
            btnBook = itemView.findViewById(R.id.btnBook);
        }
    }
}
