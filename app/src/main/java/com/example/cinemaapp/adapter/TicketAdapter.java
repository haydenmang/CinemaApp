package com.example.cinemaapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.cinemaapp.R;
import com.example.cinemaapp.TicketActivity;
import com.example.cinemaapp.data.model.Booking;
import com.example.cinemaapp.data.model.BookingSeat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Date;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private List<Booking> bookings = new ArrayList<>();
    private final Context context;

    public TicketAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Booking> data) {
        this.bookings = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        
        String title = "N/A";
        String cinemaName = "N/A";
        String time = "N/A";
        String date = "N/A";
        String posterUrl = "";
        
        if (booking.showtime != null) {
            if (booking.showtime.movie != null) {
                title = booking.showtime.movie.getTitle();
                posterUrl = booking.showtime.movie.getPosterUrl();
            }
            if (booking.showtime.room != null && booking.showtime.room.cinema != null) {
                cinemaName = booking.showtime.room.cinema.getName();
            }
            
            String startTime = booking.showtime.getStartTime();
            if (startTime != null) {
                try {
                    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    Date d = input.parse(startTime);
                    if (d != null) {
                        time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(d);
                        date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(d);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        holder.txtMovieTitle.setText(title);
        holder.txtCinemaName.setText(cinemaName);
        holder.txtTime.setText(time);
        holder.txtDate.setText(date);
        
        List<String> seats = new ArrayList<>();
        if (booking.bookingSeats != null) {
            for (BookingSeat bs : booking.bookingSeats) {
                seats.add(bs.seatNumber);
            }
        }
        holder.txtSeat.setText(String.join(", ", seats));

        if (posterUrl != null && !posterUrl.isEmpty()) {
            Glide.with(context)
                    .load(posterUrl)
                    .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(16)))
                    .into(holder.imgPoster);
        } else {
            holder.imgPoster.setImageResource(android.R.drawable.ic_menu_gallery);
        }
        
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TicketActivity.class);
            intent.putExtra("movie_title", holder.txtMovieTitle.getText().toString());
            String fullShowtime = booking.showtime != null ? booking.showtime.getStartTime() : "";
            intent.putExtra("showtime", fullShowtime);
            intent.putStringArrayListExtra("selected_seats", new ArrayList<>(seats));
            intent.putExtra("total_price", (long) booking.totalPrice);
            intent.putExtra("payment_method", "Đã thanh toán");
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bookings != null ? bookings.size() : 0;
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView txtMovieTitle, txtCinemaName, txtTime, txtDate, txtSeat;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgTicketPoster);
            txtMovieTitle = itemView.findViewById(R.id.txtTicketMovieTitle);
            txtCinemaName = itemView.findViewById(R.id.txtTicketCinemaName);
            txtTime = itemView.findViewById(R.id.txtTicketTime);
            txtDate = itemView.findViewById(R.id.txtTicketDate);
            txtSeat = itemView.findViewById(R.id.txtTicketSeat);
        }
    }
}
