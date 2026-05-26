package com.example.cinemaapp.data.model;

import com.google.gson.annotations.SerializedName;

public class Cinema {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("address")
    private String address;

    // Getters và Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
}