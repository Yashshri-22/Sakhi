package com.example.sakhi;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "sakhi_session";
    private static final String KEY_ACCESS = "access_token";
    private static final String KEY_REFRESH = "refresh_token";
    private static final String KEY_USER_ID = "user_id";

    // ✅ Save access & refresh token
    public static void saveSession(Context context, String access, String refresh) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sp.edit()
                .putString(KEY_ACCESS, access)
                .putString(KEY_REFRESH, refresh)
                .apply();
    }

    // ✅ THIS IS saveUserId (YOU WERE ASKING ABOUT THIS)
    public static void saveUserId(Context context, String userId) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sp.edit()
                .putString(KEY_USER_ID, userId)
                .apply();
    }

    // ✅ Getter for access token
    public static String getAccessToken(Context context) {
        return context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(KEY_ACCESS, null);
    }

    // ✅ Getter for user id
    public static String getUserId(Context context) {
        return context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(KEY_USER_ID, null);
    }

    // ✅ Login check
    public static boolean isLoggedIn(Context context) {
        return getAccessToken(context) != null;
    }

    // ✅ Logout
    public static void clearSession(Context context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }
}
