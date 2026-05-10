package com.example.cinemaapp.data.model;

import com.google.gson.annotations.SerializedName;

public class Movie {
    public int id;
    public String title;
    public String description;
    public int duration;
    @SerializedName("poster_url")
    public String posterUrl;
    @SerializedName("release_date")
    public String releaseDate;
}
