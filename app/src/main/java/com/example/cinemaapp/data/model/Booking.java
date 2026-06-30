package com.example.cinemaapp.data.model;

import com.google.gson.annotations.SerializedName;

public class Booking {
    public Integer id;
    @SerializedName("user_id")
    public int userId;
    @SerializedName("showtime_id")
    public int showtimeId;
    @SerializedName("total_price")
    public double totalPrice;
    public String status;
    @SerializedName("created_at")
    public String createdAt;

    @SerializedName("showtimes")
    public Showtime showtime;
    @SerializedName("booking_seats")
    public java.util.List<BookingSeat> bookingSeats;
}
