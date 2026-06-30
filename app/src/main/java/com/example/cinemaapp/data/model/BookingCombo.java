package com.example.cinemaapp.data.model;

import com.google.gson.annotations.SerializedName;

public class BookingCombo {
    public Integer id;
    @SerializedName("booking_id")
    public int bookingId;
    @SerializedName("combo_id")
    public int comboId;
    public int quantity;
}
