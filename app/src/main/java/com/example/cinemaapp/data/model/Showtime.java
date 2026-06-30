package com.example.cinemaapp.data.model;

import com.google.gson.annotations.SerializedName;

public class Showtime {
    @SerializedName("id")
    private int id;

    @SerializedName("movie_id")
    private int movieId;

    @SerializedName("room_id")
    private int roomId;

    @SerializedName("start_time")
    private String startTime;

    @SerializedName("price")
    private double price;

    @SerializedName("movies")
    public Movie movie;

    @SerializedName("rooms")
    public Room room;

    // Getters và Setters
    public int getId() { return id; }
    public int getMovieId() { return movieId; }
    public int getRoomId() { return roomId; }
    public String getStartTime() { return startTime; }
    public double getPrice() { return price; }

    public void setId(int id) { this.id = id; }
    public void setMovieId(int movieId) { this.movieId = movieId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setPrice(double price) { this.price = price; }
}