package com.example.cinemaapp.data.model;

import com.google.gson.annotations.SerializedName;

public class Combo {
    public Integer id;
    public String name;
    public String description;
    public double price;
    @SerializedName("image_url")
    public String imageUrl;
}
