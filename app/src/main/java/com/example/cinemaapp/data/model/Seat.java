package com.example.cinemaapp.data.model;

import com.google.gson.annotations.SerializedName;

public class Seat {
    public int id;
    @SerializedName("room_id")
    public int roomId;
    @SerializedName("seat_number")
    public String seatNumber;
    public String type;
}
