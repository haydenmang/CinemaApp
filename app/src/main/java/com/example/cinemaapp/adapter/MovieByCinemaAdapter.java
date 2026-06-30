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

    public interface OnShowtimeClickListener {
        void onShowtimeClick(Movie movie, Showtime showtime);
    }

    private List<Movie> movies = new ArrayList<>();
    private Map<Integer, List<Showtime>> showtimeMap;
    private OnShowtimeClickListener listener;

    public void setData(List<Movie> movies, Map<Integer, List<Showtime>> showtimeMap) {
        this.movies = movies;
        this.showtimeMap = showtimeMap;
        notifyDataSetChanged();
    }

    public void setOnShowtimeClickListener(OnShowtimeClickListener listener) {
        this.listener = listener;
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
        holder.llRoomTypesContainer.removeAllViews();

        List<Showtime> showtimes = showtimeMap != null ? showtimeMap.get(movie.getId()) : null;
        if (showtimes != null) {
            Map<String, List<Showtime>> groupedByRoomType = new java.util.LinkedHashMap<>();
            
            java.util.Collections.sort(showtimes, (a, b) -> {
                if (a.getStartTime() == null || b.getStartTime() == null) return 0;
                return a.getStartTime().compareTo(b.getStartTime());
            });

            for (Showtime st : showtimes) {
                String type = (st.room != null && st.room.roomType != null && !st.room.roomType.trim().isEmpty()) ? st.room.roomType : "2D Tiêu chuẩn";
                if (!groupedByRoomType.containsKey(type)) {
                    groupedByRoomType.put(type, new ArrayList<>());
                }
                groupedByRoomType.get(type).add(st);
            }

            for (Map.Entry<String, List<Showtime>> entry : groupedByRoomType.entrySet()) {
                TextView tvRoomType = new TextView(holder.itemView.getContext());
                tvRoomType.setText(entry.getKey());
                tvRoomType.setTextColor(0xFFFFFFFF);
                tvRoomType.setTextSize(14f);
                tvRoomType.setTypeface(null, android.graphics.Typeface.BOLD);
                tvRoomType.setPadding(0, 16, 0, 8);
                holder.llRoomTypesContainer.addView(tvRoomType);

                ChipGroup chipGroup = new ChipGroup(holder.itemView.getContext());
                chipGroup.setChipSpacingHorizontal(15);
                chipGroup.setChipSpacingVertical(10);
                
                java.util.Set<String> addedTimes = new java.util.HashSet<>();
                for (Showtime st : entry.getValue()) {
                    String timeStr = formatTime(st.getStartTime());
                    if (addedTimes.contains(timeStr)) continue;
                    addedTimes.add(timeStr);
                    
                    Chip chip = new Chip(holder.itemView.getContext());
                    chip.setText(timeStr);
                    chip.setChipBackgroundColorResource(R.color.chip_bg);
                    chip.setTextColor(0xFFFFFFFF);
                    chip.setTextSize(13f);
                    chip.setChipCornerRadius(8f);
                    chip.setEnsureMinTouchTargetSize(false);
                    chip.setPadding(4, 0, 4, 0);
                    chip.setOnClickListener(v -> {
                        if (listener != null) listener.onShowtimeClick(movie, st);
                    });
                    chipGroup.addView(chip);
                }
                holder.llRoomTypesContainer.addView(chipGroup);
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
        android.widget.LinearLayout llRoomTypesContainer;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMovieTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvDuration   = itemView.findViewById(R.id.tvDuration);
            llRoomTypesContainer = itemView.findViewById(R.id.llRoomTypesContainer);
        }
    }
}
