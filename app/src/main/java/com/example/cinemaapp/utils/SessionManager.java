package com.example.cinemaapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.cinemaapp.data.model.User;

public class SessionManager {
    private static final String PREF_NAME = "CinemaAppSession";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_PHONE = "user_phone";
    private static final String KEY_USER_EMAIL = "user_email";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveUserSession(User user) {
        if (user != null) {
            editor.putInt(KEY_USER_ID, user.id);
            editor.putString(KEY_USER_NAME, user.name);
            editor.putString(KEY_USER_PHONE, user.phone);
            editor.putString(KEY_USER_EMAIL, user.email);
            editor.apply();
        }
    }

    public User getUserSession() {
        if (!pref.contains(KEY_USER_ID)) {
            return null;
        }
        User user = new User();
        user.id = pref.getInt(KEY_USER_ID, -1);
        user.name = pref.getString(KEY_USER_NAME, "");
        user.phone = pref.getString(KEY_USER_PHONE, "");
        user.email = pref.getString(KEY_USER_EMAIL, "");
        return user;
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
