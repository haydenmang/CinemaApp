package com.example.cinemaapp;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cinemaapp.data.api.ApiService;
import com.example.cinemaapp.data.api.SupabaseClient;
import com.example.cinemaapp.data.model.Cinema;
import com.example.cinemaapp.data.model.Movie;
import com.example.cinemaapp.data.model.Showtime;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}