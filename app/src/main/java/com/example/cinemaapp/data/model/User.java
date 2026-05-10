package com.example.cinemaapp.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @Expose(serialize = false, deserialize = true)
    public int id;

    @Expose
    public String name;

    @Expose
    public String email;

    @Expose
    public String password;

    @Expose
    public String phone;

    @SerializedName("created_at")
    @Expose(serialize = false, deserialize = true)
    public String createdAt;
}
