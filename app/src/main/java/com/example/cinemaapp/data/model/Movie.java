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

    @SerializedName("age_limit")
    private String ageLimit;

    @SerializedName("trailer")
    private String trailer;

    @SerializedName("genre")
    private String genre;

    @SerializedName("director")
    private String director;

    @SerializedName("cast_list")
    private String castList;

    @SerializedName("rating")
    private double rating;

    //Getter để lấy dữ liệu
    public int getId() {return id; }
    public String getTitle() {return title; }
    public String getDescription() {return description; }
    public int getDuration() {return duration; }
    public String getPosterUrl() {return posterUrl; }
    public String getReleaseDate() {return releaseDate; }
    public String getAgeLimit() {return ageLimit; }
    public String getTrailer() {return trailer; }
    public String getGenre() {return genre; }
    public String getDirector() {return director; }
    public String getCastList() {return castList; }
    public double getRating() {return rating; }

    //Setter để gán dữ liệu
    public void setId(int id) {this.id = id; }
    public void setTitle(String title) {this.title = title; }
    public void setDescription(String description) {this.description = description;}
    public void setDuration(int duration) {this.duration = duration;}
    public void setPosterUrl(String posterUrl) {this.posterUrl = posterUrl;}
    public void setReleaseDate(String releaseDate) {this.releaseDate = releaseDate;}
    public void setAgeLimit(String ageLimit) {this.ageLimit = ageLimit; }
    public void setTrailer(String trailer) {this.trailer = trailer; }
    public void setGenre(String genre) {this.genre = genre; }
    public void setDirector(String director) {this.director = director; }
    public void setCastList(String castList) {this.castList = castList; }
    public void setRating(double rating) {this.rating = rating; }

}
