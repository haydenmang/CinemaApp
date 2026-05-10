package com.example.cinemaapp.data.model;

import com.google.gson.annotations.SerializedName;

public class Payment {
    public int id;
    @SerializedName("booking_id")
    public int bookingId;
    public double amount;
    public String method;
    public String status;
    @SerializedName("transaction_code")
    public String transactionCode;
    @SerializedName("created_at")
    public String createdAt;
}
