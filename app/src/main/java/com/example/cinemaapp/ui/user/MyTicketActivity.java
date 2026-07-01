package com.example.cinemaapp.ui.user;

import com.example.cinemaapp.R;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MyTicketActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_ticket);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupBottomNavigation();
        
        RecyclerView recyclerView = findViewById(R.id.recyclerViewTickets);
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        com.example.cinemaapp.adapter.TicketAdapter adapter = new com.example.cinemaapp.adapter.TicketAdapter(this);
        recyclerView.setAdapter(adapter);
        
        com.example.cinemaapp.utils.SessionManager sessionManager = new com.example.cinemaapp.utils.SessionManager(this);
        com.example.cinemaapp.data.model.User user = sessionManager.getUserSession();
        if (user != null) {
            com.example.cinemaapp.data.api.ApiService apiService = com.example.cinemaapp.data.api.SupabaseClient.getClient().create(com.example.cinemaapp.data.api.ApiService.class);
            // Fetch bookings with nested showtimes (which has movies and rooms(which has cinemas)) and booking_seats
            String selectQuery = "*, showtimes(*, movies(*), rooms(*, cinemas(*))), booking_seats(*)";
            apiService.getBookingsByUserWithDetails("eq." + user.id, selectQuery, "created_at.desc").enqueue(new retrofit2.Callback<java.util.List<com.example.cinemaapp.data.model.Booking>>() {
                @Override
                public void onResponse(retrofit2.Call<java.util.List<com.example.cinemaapp.data.model.Booking>> call, retrofit2.Response<java.util.List<com.example.cinemaapp.data.model.Booking>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        adapter.setData(response.body());
                    } else {
                        android.widget.Toast.makeText(MyTicketActivity.this, "Không thể tải vé", android.widget.Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<java.util.List<com.example.cinemaapp.data.model.Booking>> call, Throwable t) {
                    android.widget.Toast.makeText(MyTicketActivity.this, "Lỗi kết nối", android.widget.Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    
    private void setupBottomNavigation() {
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }
}