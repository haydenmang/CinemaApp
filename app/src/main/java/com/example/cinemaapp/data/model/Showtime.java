package com.example.cinemaapp.data.model;

import com.google.gson.annotations.SerializedName;

public class Showtime {
    public int id;
    @SerializedName("movie_id")
    public int movieId;
    @SerializedName("room_id")
    public int roomId;
    @SerializedName("start_time")
    public String startTime;
    public double price;
}
