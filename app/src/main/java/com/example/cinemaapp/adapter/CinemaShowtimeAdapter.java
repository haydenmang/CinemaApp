package com.example.cinemaapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cinemaapp.R;
import com.example.cinemaapp.data.model.Cinema;
import com.example.cinemaapp.ui.MovieByCinemaActivity;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;

public class CinemaShowtimeAdapter extends RecyclerView.Adapter<CinemaShowtimeAdapter.ViewHolder> {

    public interface OnCinemaClickListener {
        void onCinemaClick(Cinema cinema);
    }

    private List<Cinema> cinemas = new ArrayList<>();
    private final OnCinemaClickListener listener;

    public CinemaShowtimeAdapter(OnCinemaClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<Cinema> cinemas) {
        this.cinemas = cinemas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cinema_showtime, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cinema cinema = cinemas.get(position);
        holder.tvCinemaName.setText(cinema.getName());
        holder.tvCinemaAddress.setText(cinema.getAddress());
        // Ẩn chip giờ chiếu — màn hình này chỉ chọn rạp
        holder.cgShowtimes.setVisibility(View.GONE);
        holder.divider.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCinemaClick(cinema);
            }
        });
    }

    @Override
    public int getItemCount() { return cinemas.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCinemaName, tvCinemaAddress;
        ChipGroup cgShowtimes;
        View divider;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCinemaName    = itemView.findViewById(R.id.tvCinemaName);
            tvCinemaAddress = itemView.findViewById(R.id.tvCinemaAddress);
            cgShowtimes     = itemView.findViewById(R.id.cgShowtimes);
            divider         = itemView.findViewById(R.id.dividerShowtime);
        }
    }
}
