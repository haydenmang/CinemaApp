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

    @GET("showtimes")
    Call<List<Showtime>> getShowtimesByDateRangePaginated(
            @Header("Range") String range,
            @Query("start_time") String gteDate,
            @Query("start_time") String lteDate
    );





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

    @GET("rooms")
    Call<List<Room>> getAllRooms();

    // ===== SEATS =====
    @GET("seats")
    Call<List<Seat>> getSeatsByRoom(@Query("room_id") String roomIdFilter);

    // ===== SHOWTIMES =====
    @GET("showtimes")
    Call<List<Showtime>> getShowtimesByRooms(@Query(value = "room_id", encoded = true) String roomIdsFilter);

    @GET("showtimes")
    Call<List<Showtime>> getShowtimesByRoomsAndDate(
            @Query(value = "room_id", encoded = true) String roomIdsFilter,
            @Query("start_time") String gteDate,
            @Query("start_time") String lteDate
    );
    @GET("showtimes")
    Call<List<Showtime>> getShowtimesByMovie(@Query("movie_id") String movieIdFilter);

    @GET("showtimes")
    Call<List<Showtime>> getAllShowtimes();

    @GET("showtimes")
    Call<List<Showtime>> getShowtimesByDateRange(
            @Query("start_time") String gteDate,
            @Query("start_time") String lteDate
    );

    @GET("showtimes")
    Call<List<Showtime>> getShowtimesByRoom(@Query("room_id") String roomIdFilter);

    // ===== BOOKINGS =====
    @GET("bookings")
    Call<List<Booking>> getBookingsByUser(@Query("user_id") String userIdFilter);

    @GET("bookings")
    Call<List<Booking>> getBookingsByUserWithDetails(
        @Query("user_id") String userIdFilter,
        @Query("select") String selectQuery,
        @Query("order") String order
    );

    @POST("bookings")
    Call<Void> createBooking(@Body Booking booking);

    @POST("bookings")
    @Headers("Prefer: return=representation")
    Call<List<Booking>> createBookingReturning(@Body Booking booking);

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
    // ===== COMBOS =====
    @GET("combos")
    Call<List<Combo>> getCombos();

    // ===== BOOKING COMBOS =====
    @POST("booking_combos")
    Call<Void> createBookingCombo(@Body BookingCombo bookingCombo);
}
