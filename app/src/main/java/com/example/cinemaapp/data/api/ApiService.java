package com.example.cinemaapp.data.api;

import com.example.cinemaapp.data.model.*;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;
import retrofit2.http.Headers;

public interface ApiService {

    // ===== MOVIES =====
    @GET("movies")
    Call<List<Movie>> getMovies(
            @Query("order") String order,
            @Query("limit") int limit
    );

    @GET("movies")
    Call<List<Movie>> getMovieById(@Query("id") String idFilter); // id=eq.1



    // ===== USERS =====
    @GET("users")
    Call<List<User>> getUserByEmail(@Query("email") String emailFilter);

    @GET("users")
    Call<List<User>> getUserByPhone(@Query("phone") String phoneFilter); // email=eq.abc@gmail.com

    @POST("users")
    @Headers("Content-Type: application/json")
    Call<Void> createUser(@Body User user);

    // ===== CINEMAS =====
    @GET("cinemas")
    Call<List<Cinema>> getCinemas();

    // ===== ROOMS =====
    @GET("rooms")
    Call<List<Room>> getRoomsByCinema(@Query("cinema_id") String cinemaIdFilter);

    // ===== SEATS =====
    @GET("seats")
    Call<List<Seat>> getSeatsByRoom(@Query("room_id") String roomIdFilter);

    // ===== SHOWTIMES =====
    @GET("showtimes")
    Call<List<Showtime>> getShowtimesByMovie(@Query("movie_id") String movieIdFilter);

    @GET("showtimes")
    Call<List<Showtime>> getShowtimesByRoom(@Query("room_id") String roomIdFilter);

    // ===== BOOKINGS =====
    @GET("bookings")
    Call<List<Booking>> getBookingsByUser(@Query("user_id") String userIdFilter);

    @POST("bookings")
    Call<Void> createBooking(@Body Booking booking);

    // ===== BOOKING SEATS =====
    @GET("booking_seats")
    Call<List<BookingSeat>> getBookedSeatsByShowtime(@Query("showtime_id") String showtimeIdFilter);

    @POST("booking_seats")
    Call<Void> createBookingSeat(@Body BookingSeat bookingSeat);

    // ===== PAYMENTS =====
    @POST("payments")
    Call<Void> createPayment(@Body Payment payment);

    @GET("payments")
    Call<List<Payment>> getPaymentByBooking(@Query("booking_id") String bookingIdFilter);
}
