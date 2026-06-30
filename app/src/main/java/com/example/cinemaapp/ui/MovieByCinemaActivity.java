package com.example.cinemaapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.example.cinemaapp.R;
import com.example.cinemaapp.RoomSelectionActivity;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cinemaapp.adapter.MovieByCinemaAdapter;
import com.example.cinemaapp.data.api.ApiService;
import com.example.cinemaapp.data.api.SupabaseClient;
import com.example.cinemaapp.data.model.Movie;
import com.example.cinemaapp.data.model.Room;
import com.example.cinemaapp.data.model.Showtime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieByCinemaActivity extends AppCompatActivity {

    private static final String TAG = "MovieByCinema";

    private int cinemaId;
    private ApiService apiService;
    private MovieByCinemaAdapter adapter;

    private List<Room> cinemaRooms = new ArrayList<>();
    private List<Showtime> allShowtimes = new ArrayList<>();
    private List<Movie> allMovies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_by_cinema);

        cinemaId = getIntent().getIntExtra("cinema_id", -1);
        String cinemaName = getIntent().getStringExtra("cinema_name");

        TextView tvTitle = findViewById(R.id.tvCinemaTitle);
        int testId = getIntent().getIntExtra("MOVIE_ID", -1);
        tvTitle.setText(cinemaName != null ? cinemaName : "Phim đang chiếu");

        // Hiển thị ảnh giới thiệu rạp
        ImageView ivBanner = findViewById(R.id.ivCinemaBanner);
        ivBanner.setImageResource(getCinemaBanner(cinemaName));

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        RecyclerView rvMovies = findViewById(R.id.rvMovies);
        adapter = new MovieByCinemaAdapter();
        adapter.setOnShowtimeClickListener((movie, showtime) -> {
            Intent intent = new Intent(this, com.example.cinemaapp.SeatSelectionActivity.class);
            intent.putExtra("movie_title", movie.getTitle());
            intent.putExtra("showtime", showtime.getStartTime());
            intent.putExtra("showtime_id", showtime.getId());
            intent.putExtra("cinema_name", cinemaName);
            intent.putExtra("cinema_address", getIntent().getStringExtra("cinema_address"));
            if (showtime.room != null) {
                intent.putExtra("room_name", showtime.room.name);
                intent.putExtra("room_type", showtime.room.roomType);
            }
            intent.putExtra("seat_price", (long) showtime.getPrice());
            startActivity(intent);
        });
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(adapter);

        apiService = SupabaseClient.getClient().create(ApiService.class);
        loadData();
    }

    private int getCinemaBanner(String cinemaName) {
        if (cinemaName == null) return R.drawable.bg_cinema;
        String name = cinemaName.toLowerCase();
        if (name.contains("times city"))         return R.drawable.cinema_times_city;
        if (name.contains("trương định"))        return R.drawable.cinema_truong_dinh;
        if (name.contains("nguyễn chí thanh"))   return R.drawable.cinema_nguyen_chi_thanh;
        if (name.contains("royal"))              return R.drawable.cinema_royal;
        if (name.contains("trần duy hưng"))      return R.drawable.cinema_tran_duy_hung;
        if (name.contains("bà triệu"))           return R.drawable.cinema_ba_trieu;
        if (name.contains("hà đông"))            return R.drawable.cinema_aeon_ha_dong;
        if (name.contains("long biên"))          return R.drawable.cinema_aeon_long_bien;
        if (name.contains("thụy khuê"))          return R.drawable.cinema_sun_thuy_khue;
        if (name.contains("cầu giấy"))           return R.drawable.cinema_iph_cau_giay;
        return R.drawable.bg_cinema;
    }

    private void loadData() {
        // 1. Load rooms của rạp này
        apiService.getRoomsByCinema("eq." + cinemaId).enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cinemaRooms = response.body();
                }
                loadShowtimes();
            }
            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Log.e(TAG, "getRooms error", t);
                loadShowtimes();
            }
        });
    }

    private void loadShowtimes() {
        // Nếu rạp này chưa có phòng chiếu nào thì bỏ qua luôn
        if (cinemaRooms == null || cinemaRooms.isEmpty()) {
            if (allMovies.isEmpty()) loadMovies();
            else runOnUiThread(() -> buildMovieShowtimeData());
            return;
        }

        // 1. Lấy danh sách ID các phòng của rạp này (VD: ghép thành chuỗi "in.(38,39,40)")
        StringBuilder roomFilter = new StringBuilder("in.(");
        for (int i = 0; i < cinemaRooms.size(); i++) {
            roomFilter.append(cinemaRooms.get(i).id);
            if (i < cinemaRooms.size() - 1) roomFilter.append(",");
        }
        roomFilter.append(")");

        // Nhận ngày từ màn hình trước truyền sang (Mặc định lấy hôm nay nếu bị lỗi)
        java.util.Calendar today = java.util.Calendar.getInstance();
        int year = getIntent().getIntExtra("SELECTED_YEAR", today.get(java.util.Calendar.YEAR));
        int month = getIntent().getIntExtra("SELECTED_MONTH", today.get(java.util.Calendar.MONTH));
        int day = getIntent().getIntExtra("SELECTED_DAY", today.get(java.util.Calendar.DAY_OF_MONTH));

        // Định dạng ngày để gửi lên Supabase
        String yyyyMMdd = String.format(java.util.Locale.US, "%04d-%02d-%02d", year, month + 1, day);
        String gteDate = "gte." + yyyyMMdd + "T00:00:00";
        String lteDate = "lte." + yyyyMMdd + "T23:59:59";

        // 2. Chỉ gọi API tải đúng các suất chiếu của những phòng này TRONG NGÀY ĐÓ
        apiService.getShowtimesByRoomsAndDate(roomFilter.toString(), gteDate, lteDate).enqueue(new Callback<List<Showtime>>() {
            @Override
            public void onResponse(Call<List<Showtime>> call, Response<List<Showtime>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allShowtimes = response.body();
                } else {
                    allShowtimes.clear();
                }
                if (allMovies.isEmpty()) loadMovies();
                else runOnUiThread(() -> buildMovieShowtimeData());
            }

            @Override
            public void onFailure(Call<List<Showtime>> call, Throwable t) {
                allShowtimes.clear();
                if (allMovies.isEmpty()) loadMovies();
                else runOnUiThread(() -> buildMovieShowtimeData());
            }
        });
    }

    private void loadMovies() {
        apiService.getMovies("id.asc", 100).enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allMovies = response.body();
                }
                runOnUiThread(() -> buildMovieShowtimeData());
            }
            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                Log.e(TAG, "getMovies error", t);
                runOnUiThread(() -> buildMovieShowtimeData());
            }
        });
    }

    private void buildMovieShowtimeData() {
        int targetMovieId = getIntent().getIntExtra("MOVIE_ID", -1);
        // Room ids thuộc rạp này
        List<Integer> roomIds = new ArrayList<>();
        for (Room r : cinemaRooms) roomIds.add(r.id);

        // Showtimes thuộc rạp này, group by movie_id
        Map<Integer, List<Showtime>> movieShowtimes = new HashMap<>();
        for (Showtime st : allShowtimes) {
            if (roomIds.contains(st.getRoomId())) {
                for (Room r : cinemaRooms) {
                    if (r.id == st.getRoomId()) {
                        st.room = r;
                        break;
                    }
                }
                if (!movieShowtimes.containsKey(st.getMovieId())) {
                    movieShowtimes.put(st.getMovieId(), new ArrayList<>());
                }
                movieShowtimes.get(st.getMovieId()).add(st);
            }
        }

        // Lọc movies có suất chiếu tại rạp này
        List<Movie> moviesAtCinema = new ArrayList<>();
        for (Movie m : allMovies) {
            if (movieShowtimes.containsKey(m.getId())) {
                if (targetMovieId == -1 || m.getId() == targetMovieId) {
                    moviesAtCinema.add(m);
                }
            }
        }
        adapter.setData(moviesAtCinema, movieShowtimes);

        // Hiện empty state nếu không có phim
        RecyclerView rvMovies = findViewById(R.id.rvMovies);
        LinearLayout layoutEmpty = findViewById(R.id.layoutEmpty);
        if (moviesAtCinema.isEmpty()) {
            rvMovies.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvMovies.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }
}
