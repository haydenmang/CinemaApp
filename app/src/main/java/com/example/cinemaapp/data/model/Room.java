package com.example.cinemaapp.data.model;

import com.google.gson.annotations.SerializedName;

public class Room {
    public int id;
    @SerializedName("cinema_id")
    public int cinemaId;
    public String name;
}
