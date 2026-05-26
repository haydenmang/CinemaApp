package com.example.cinemaapp.ui.movie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinemaapp.R;
import com.example.cinemaapp.ui.movie.model.MovieItem;
import com.example.cinemaapp.ui.movie.util.MovieIntentHelper;
import com.example.cinemaapp.ui.movie.util.PosterImageLoader;

import java.util.ArrayList;
import java.util.List;

public class MoviePagerAdapter extends RecyclerView.Adapter<MoviePagerAdapter.PagerViewHolder> {

    private static final int LOOP_COUNT = 400;

    private List<MovieItem> movieList = new ArrayList<>();

    public MoviePagerAdapter(List<MovieItem> movieList) {
        if (movieList != null) {
            this.movieList = new ArrayList<>(movieList);
        }
    }

    public void updateMovies(List<MovieItem> movies) {
        movieList = movies != null ? new ArrayList<>(movies) : new ArrayList<>();
        notifyDataSetChanged();
    }

    public int getMiddlePosition() {
        if (movieList.isEmpty()) {
            return 0;
        }
        int bucket = LOOP_COUNT / 2;
        return bucket * movieList.size();
    }

    public int getRealPosition(int adapterPosition) {
        if (movieList.isEmpty()) {
            return 0;
        }
        int pos = adapterPosition % movieList.size();
        return pos < 0 ? pos + movieList.size() : pos;
    }

    public MovieItem getMovieAt(int adapterPosition) {
        if (movieList.isEmpty()) {
            return null;
        }
        return movieList.get(getRealPosition(adapterPosition));
    }

    @NonNull
    @Override
    public PagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_poster, parent, false);
        return new PagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PagerViewHolder holder, int position) {
        if (movieList.isEmpty()) {
            return;
        }

        MovieItem movie = getMovieAt(position);
        Context context = holder.itemView.getContext();

        PosterImageLoader.load(context, movie.getPosterUrl(), holder.imgPoster);

        String ageRating = movie.getAgeRating();
        holder.tvAgeRating.setText(ageRating != null ? ageRating : "P");
        if (holder.tvRating != null) {
            holder.tvRating.setText(
                    context.getString(R.string.movie_rating_star, movie.getRatingText())
            );
        }

        holder.itemView.setOnClickListener(v ->
                context.startActivity(MovieIntentHelper.newDetailIntent(context, movie))
        );
    }

    @Override
    public void onViewRecycled(@NonNull PagerViewHolder holder) {
        super.onViewRecycled(holder);
        PosterImageLoader.clear(holder.itemView.getContext(), holder.imgPoster);
    }

    @Override
    public int getItemCount() {
        if (movieList.isEmpty()) {
            return 0;
        }
        return movieList.size() * LOOP_COUNT;
    }

    static class PagerViewHolder extends RecyclerView.ViewHolder {

        final ImageView imgPoster;
        final TextView tvAgeRating;
        final TextView tvRating;

        PagerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgPoster);
            tvAgeRating = itemView.findViewById(R.id.tvAgeRating);
            tvRating = itemView.findViewById(R.id.tvRating);
        }
    }
}
