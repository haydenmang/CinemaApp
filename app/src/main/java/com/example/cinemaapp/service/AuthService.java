package com.example.cinemaapp.service;

import com.example.cinemaapp.data.model.User;
import com.example.cinemaapp.data.repository.UserRepository;

public class AuthService {

    private final UserRepository userRepository;

    public AuthService() {
        userRepository = new UserRepository();
    }

    public interface AuthCallback {
        void onSuccess(User user);
        void onError(String message);
    }

    public void login(String phone, String password, AuthCallback callback) {
        userRepository.getUserByPhone(phone, new UserRepository.Callback1<User>() {
            @Override
            public void onResult(User user) {
                if (user == null) {
                    callback.onError("Số điện thoại không tồn tại");
                } else if (!user.password.equals(password)) {
                    callback.onError("Mật khẩu không đúng");
                } else {
                    callback.onSuccess(user);
                }
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void signUp(User userInfo, AuthCallback callback) {
        // Bước 1: kiểm tra email đã tồn tại chưa
        userRepository.checkExistEmail(userInfo.email, new UserRepository.Callback1<Boolean>() {
            @Override
            public void onResult(Boolean emailExists) {
                if (emailExists) {
                    callback.onError("Email đã tồn tại");
                    return;
                }
                // Bước 2: kiểm tra SĐT đã tồn tại chưa
                userRepository.getUserByPhone(userInfo.phone, new UserRepository.Callback1<User>() {
                    @Override
                    public void onResult(User existing) {
                        if (existing != null) {
                            callback.onError("Số điện thoại đã được đăng ký");
                            return;
                        }
                        // Bước 3: tạo tài khoản
                        userRepository.createUser(userInfo, new UserRepository.Callback1<Boolean>() {
                            @Override
                            public void onResult(Boolean success) {
                                callback.onSuccess(userInfo);
                            }
                            @Override
                            public void onError(String message) {
                                callback.onError(message);
                            }
                        });
                    }
                    @Override
                    public void onError(String message) {
                        callback.onError(message);
                    }
                });
            }
            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }
}
