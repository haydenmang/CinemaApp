package com.example.cinemaapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cinemaapp.R;
import com.example.cinemaapp.data.model.Movie;
import com.example.cinemaapp.data.model.Showtime;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MovieByCinemaAdapter extends RecyclerView.Adapter<MovieByCinemaAdapter.ViewHolder> {

    private List<Movie> movies = new ArrayList<>();
    private Map<Integer, List<Showtime>> showtimeMap;

    public void setData(List<Movie> movies, Map<Integer, List<Showtime>> showtimeMap) {
        this.movies = movies;
        this.showtimeMap = showtimeMap;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_showtime, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.tvMovieTitle.setText(movie.getTitle());
        holder.tvDuration.setText(movie.getDuration() + " phút");
        holder.cgShowtimes.removeAllViews();

        List<Showtime> showtimes = showtimeMap != null ? showtimeMap.get(movie.getId()) : null;
        if (showtimes != null) {
            java.util.ArrayList<String> uniqueTimes = new java.util.ArrayList<>();

            for (Showtime st : showtimes) {
                String timeStr = formatTime(st.getStartTime());
                if (!uniqueTimes.contains(timeStr)) {
                    uniqueTimes.add(timeStr);
                }
            }
            // BẢO BỐI Ở ĐÂY: Tự động sắp xếp giờ từ nhỏ đến lớn (09:30 -> 14:00 -> 19:15)
            java.util.Collections.sort(uniqueTimes);
            for (String timeStr : uniqueTimes) {
                Chip chip = new Chip(holder.itemView.getContext());
                chip.setText(timeStr);
                chip.setChipBackgroundColorResource(R.color.chip_bg);
                chip.setTextColor(0xFFFFFFFF);
                chip.setTextSize(13f);
                chip.setChipCornerRadius(8f);
                chip.setEnsureMinTouchTargetSize(false);
                chip.setPadding(4, 0, 4, 0);

                // Mở giao diện Chọn phòng của Huy
                chip.setOnClickListener(v -> {
                    android.content.Intent intent = new android.content.Intent(holder.itemView.getContext(), com.example.cinemaapp.RoomSelectionActivity.class);
                    intent.putExtra("movie_title", movie.getTitle());
                    intent.putExtra("showtime", timeStr);
                    holder.itemView.getContext().startActivity(intent);
                });

                holder.cgShowtimes.addView(chip);
            }
        }
    }

    private String formatTime(String startTime) {
        if (startTime == null) return "";
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = input.parse(startTime);
            return date != null ? output.format(date) : startTime;
        } catch (Exception e) {
            if (startTime.length() >= 16) return startTime.substring(11, 16);
            return startTime;
        }
    }

    @Override
    public int getItemCount() { return movies.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMovieTitle, tvDuration;
        ChipGroup cgShowtimes;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMovieTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvDuration   = itemView.findViewById(R.id.tvDuration);
            cgShowtimes  = itemView.findViewById(R.id.cgShowtimes);
        }
    }
}
