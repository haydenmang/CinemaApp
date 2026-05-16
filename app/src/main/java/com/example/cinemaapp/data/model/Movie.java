package com.example.cinemaapp.data.model;

import com.google.gson.annotations.SerializedName;

public class Movie {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("duration")
    private int duration;

    @SerializedName("poster_url")
    private String posterUrl;

    @SerializedName("release_date")
    private String releaseDate;

    //Getter để lấy dữ liệu
    public int getId() {return id; }
    public String getTitle() {return title; }
    public String getDescription() {return description; }
    public int getDuration() {return duration; }
    public String getPosterUrl() {return posterUrl; }
    public String getReleaseDate() {return releaseDate; }

    //Setter để gán dữ liệu
    public void setId(int id) {this.id = id; }
    public void setTitle(String title) {this.title = title; }
    public void setDescription(String description) {this.description = description;}
    public void setDuration(int duration) {this.duration = duration;}
    public void setPosterUrl(String posterUrl) {this.posterUrl = posterUrl;}
    public void setReleaseDate(String releaseDate) {this.releaseDate = releaseDate;}

}
