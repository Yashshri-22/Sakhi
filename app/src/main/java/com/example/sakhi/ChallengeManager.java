package com.example.sakhi;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChallengeManager {

    private static final String PREFS = "daily_challenge_prefs";
    private static final String KEY_LAST_DATE = "last_completed_date";
    private static final String KEY_POINTS = "total_points";

    public static boolean isTodayCompleted(Context context) {
        SharedPreferences sp =
                context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        String savedDate = sp.getString(KEY_LAST_DATE, "");
        String today = getToday();

        return today.equals(savedDate);
    }

    public static void completeToday(Context context) {
        SharedPreferences sp =
                context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        int points = sp.getInt(KEY_POINTS, 0);

        sp.edit()
                .putString(KEY_LAST_DATE, getToday())
                .putInt(KEY_POINTS, points + 10)
                .apply();
    }

    public static int getPoints(Context context) {
        return context
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getInt(KEY_POINTS, 0);
    }

    private static String getToday() {
        return new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
        ).format(new Date());
    }
}
