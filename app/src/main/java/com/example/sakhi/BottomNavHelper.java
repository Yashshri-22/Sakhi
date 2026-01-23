package com.example.sakhi;

import android.app.Activity;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class BottomNavHelper {

    public static void setupBottomNav(Activity activity, int selectedNavId) {

        LinearLayout navHome = activity.findViewById(R.id.navHome);
        LinearLayout navPeriod = activity.findViewById(R.id.navPeriod);
        LinearLayout navSurvey = activity.findViewById(R.id.navSurvey);
        LinearLayout navNearby = activity.findViewById(R.id.navNearby);
        LinearLayout navChat = activity.findViewById(R.id.navChat);

        ImageView iconHome = activity.findViewById(R.id.iconHome);
        ImageView iconPeriod = activity.findViewById(R.id.iconPeriod);
        ImageView iconSurvey = activity.findViewById(R.id.iconSurvey);
        ImageView iconNearby = activity.findViewById(R.id.iconNearby);
        ImageView iconChat = activity.findViewById(R.id.iconChat);

        TextView textHome = activity.findViewById(R.id.textHome);
        TextView textPeriod = activity.findViewById(R.id.textPeriod);
        TextView textSurvey = activity.findViewById(R.id.textSurvey);
        TextView textNearby = activity.findViewById(R.id.textNearby);
        TextView textChat = activity.findViewById(R.id.textChat);

        // ✅ FULL SAFETY CHECK
        if (navHome == null || navPeriod == null || navSurvey == null
                || navNearby == null || navChat == null) return;

        int active = ContextCompat.getColor(activity, R.color.pink_primary);
        int inactive = ContextCompat.getColor(activity, R.color.nav_inactive);

        iconHome.setColorFilter(inactive);
        iconPeriod.setColorFilter(inactive);
        iconSurvey.setColorFilter(inactive);
        iconNearby.setColorFilter(inactive);
        iconChat.setColorFilter(inactive);

        textHome.setTextColor(inactive);
        textPeriod.setTextColor(inactive);
        textSurvey.setTextColor(inactive);
        textNearby.setTextColor(inactive);
        textChat.setTextColor(inactive);

        if (selectedNavId == R.id.navHome) {
            iconHome.setColorFilter(active);
            textHome.setTextColor(active);
        } else if (selectedNavId == R.id.navPeriod) {
            iconPeriod.setColorFilter(active);
            textPeriod.setTextColor(active);
        } else if (selectedNavId == R.id.navSurvey) {
            iconSurvey.setColorFilter(active);
            textSurvey.setTextColor(active);
        } else if (selectedNavId == R.id.navNearby) {
            iconNearby.setColorFilter(active);
            textNearby.setTextColor(active);
        } else if (selectedNavId == R.id.navChat) {
            iconChat.setColorFilter(active);
            textChat.setTextColor(active);
        }

        navHome.setOnClickListener(v -> {
            Intent i = new Intent(activity, HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(i);
        });

        navPeriod.setOnClickListener(v -> {
            Intent i = new Intent(activity, PeriodCalendarActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(i);
        });

        navSurvey.setOnClickListener(v -> {
            Intent i = new Intent(activity, SymptomChatActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(i);
        });

        navNearby.setOnClickListener(v -> {
            Intent i = new Intent(activity, NearbyCareActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(i);
        });

        navChat.setOnClickListener(v -> {
            Intent i = new Intent(activity, ChatActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(i);
        });
    }
}
