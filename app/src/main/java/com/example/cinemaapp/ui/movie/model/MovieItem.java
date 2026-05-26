package com.example.cinemaapp.ui.movie.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Mô hình hiển thị phim trên UI (carousel / chi tiết).
 */
public class MovieItem implements Parcelable {

    private int id;
    private String title;
    private String posterUrl;
    private String description;
    private int duration;
    private String releaseDate;
    private String ageRating;
    private String trailerUrl;
    private String genre;
    private String director;
    private String cast;
    private double rating;

    public MovieItem() {
    }

    public MovieItem(int id, String title, String posterUrl, String description,
                     int duration, String releaseDate, String ageRating, String trailerUrl,
                     String genre, String director, String cast, double rating) {
        this.id = id;
        this.title = title;
        this.posterUrl = posterUrl;
        this.description = description;
        this.duration = duration;
        this.releaseDate = releaseDate;
        this.ageRating = ageRating;
        this.trailerUrl = trailerUrl;
        this.genre = genre;
        this.director = director;
        this.cast = cast;
        this.rating = rating;
    }

    protected MovieItem(Parcel in) {
        id = in.readInt();
        title = in.readString();
        posterUrl = in.readString();
        description = in.readString();
        duration = in.readInt();
        releaseDate = in.readString();
        ageRating = in.readString();
        trailerUrl = in.readString();
        genre = in.readString();
        director = in.readString();
        cast = in.readString();
        rating = in.readDouble();
    }

    public static final Creator<MovieItem> CREATOR = new Creator<MovieItem>() {
        @Override
        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        @Override
        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(posterUrl);
        dest.writeString(description);
        dest.writeInt(duration);
        dest.writeString(releaseDate);
        dest.writeString(ageRating);
        dest.writeString(trailerUrl);
        dest.writeString(genre);
        dest.writeString(director);
        dest.writeString(cast);
        dest.writeDouble(rating);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    public String getAgeRating() { return ageRating; }
    public void setAgeRating(String ageRating) { this.ageRating = ageRating; }

    public String getTrailerUrl() { return trailerUrl; }
    public void setTrailerUrl(String trailerUrl) { this.trailerUrl = trailerUrl; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public String getCast() { return cast; }
    public void setCast(String cast) { this.cast = cast; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getRatingText() {
        if (rating <= 0) {
            return "—";
        }
        return String.format(java.util.Locale.US, "%.1f", rating);
    }
}
