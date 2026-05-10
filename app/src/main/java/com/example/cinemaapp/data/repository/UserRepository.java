package com.example.cinemaapp.data.repository;

import com.example.cinemaapp.data.api.ApiService;
import com.example.cinemaapp.data.api.SupabaseClient;
import com.example.cinemaapp.data.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private final ApiService api;

    public UserRepository() {
        api = SupabaseClient.getClient().create(ApiService.class);
    }

    public interface Callback1<T> {
        void onResult(T result);
        void onError(String message);
    }

    // Tìm user theo phone để login
    public void getUserByPhone(String phone, Callback1<User> callback) {
        api.getUserByPhone("eq." + phone).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    callback.onResult(response.body().get(0));
                } else {
                    callback.onResult(null);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Tìm user theo email để login
    public void getUserByEmail(String email, Callback1<User> callback) {
        api.getUserByEmail("eq." + email).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    callback.onResult(response.body().get(0));
                } else {
                    callback.onResult(null);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Kiểm tra email đã tồn tại chưa
    public void checkExistEmail(String email, Callback1<Boolean> callback) {
        android.util.Log.d("REGISTER", "Checking email: " + email);
        api.getUserByEmail("eq." + email).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                android.util.Log.d("REGISTER", "checkEmail code: " + response.code() + " body: " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    android.util.Log.d("REGISTER", "checkEmail size: " + response.body().size());
                    callback.onResult(!response.body().isEmpty());
                } else {
                    callback.onError("Lỗi kiểm tra email");
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                android.util.Log.e("REGISTER", "checkEmail fail: " + t.getMessage());
                callback.onError(t.getMessage());
            }
        });
    }

    // Tạo user mới
    public void createUser(User user, Callback1<Boolean> callback) {
        api.createUser(user).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                android.util.Log.d("REGISTER", "Code: " + response.code());
                try {
                    if (response.errorBody() != null) {
                        android.util.Log.e("REGISTER", "Error: " + response.errorBody().string());
                    }
                } catch (Exception e) { e.printStackTrace(); }

                if (response.isSuccessful()) {
                    callback.onResult(true);
                } else if (response.code() == 409) {
                    callback.onError("Email hoặc số điện thoại đã được đăng ký");
                } else {
                    callback.onError("Lỗi tạo tài khoản: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                android.util.Log.e("REGISTER", "Failure: " + t.getMessage());
                callback.onError(t.getMessage());
            }
        });
    }
}
