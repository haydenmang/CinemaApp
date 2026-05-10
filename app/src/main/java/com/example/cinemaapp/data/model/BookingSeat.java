package com.example.cinemaapp.data.model;

import com.google.gson.annotations.SerializedName;

public class BookingSeat {
    public int id;
    @SerializedName("booking_id")
    public int bookingId;
    @SerializedName("seat_id")
    public int seatId;
    @SerializedName("showtime_id")
    public int showtimeId;
}
