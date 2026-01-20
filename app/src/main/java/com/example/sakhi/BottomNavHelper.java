package com.example.sakhi;

import android.app.Activity;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class BottomNavHelper {

    public static void setupBottomNav(Activity activity, int activeTabId) {

        LinearLayout navHome = activity.findViewById(R.id.navHome);
        LinearLayout navSurvey = activity.findViewById(R.id.navSurvey);
        LinearLayout navNearby = activity.findViewById(R.id.navNearby);
        LinearLayout navChat = activity.findViewById(R.id.navChat);

        ImageView iconHome = activity.findViewById(R.id.iconHome);
        ImageView iconSurvey = activity.findViewById(R.id.iconSurvey);
        ImageView iconNearby = activity.findViewById(R.id.iconNearby);
        ImageView iconChat = activity.findViewById(R.id.iconChat);

        TextView textHome = activity.findViewById(R.id.textHome);
        TextView textSurvey = activity.findViewById(R.id.textSurvey);
        TextView textNearby = activity.findViewById(R.id.textNearby);
        TextView textChat = activity.findViewById(R.id.textChat);

        int active = ContextCompat.getColor(activity, R.color.pink_dark);
        int inactive = ContextCompat.getColor(activity, R.color.nav_inactive);

        // 🔁 Reset all
        iconHome.setColorFilter(inactive);
        iconSurvey.setColorFilter(inactive);
        iconNearby.setColorFilter(inactive);
        iconChat.setColorFilter(inactive);

        textHome.setTextColor(inactive);
        textSurvey.setTextColor(inactive);
        textNearby.setTextColor(inactive);
        textChat.setTextColor(inactive);

        // ⭐ Set active tab
        if (activeTabId == R.id.navHome) {
            iconHome.setColorFilter(active);
            textHome.setTextColor(active);
        } else if (activeTabId == R.id.navSurvey) {
            iconSurvey.setColorFilter(active);
            textSurvey.setTextColor(active);
        } else if (activeTabId == R.id.navNearby) {
            iconNearby.setColorFilter(active);
            textNearby.setTextColor(active);
        } else if (activeTabId == R.id.navChat) {
            iconChat.setColorFilter(active);
            textChat.setTextColor(active);
        }

        // 🚀 Navigation logic
        navHome.setOnClickListener(v -> {
            if (!(activity instanceof HomeActivity)) {
                activity.startActivity(new Intent(activity, HomeActivity.class));
                activity.finish();
            }
        });

        navSurvey.setOnClickListener(v -> {
            if (!(activity instanceof KnowYourBodyActivity)) {
                activity.startActivity(new Intent(activity, KnowYourBodyActivity.class));
                activity.finish();
            }
        });

        navNearby.setOnClickListener(v -> {
            if (!(activity instanceof NearbyCareActivity)) {
                activity.startActivity(new Intent(activity, NearbyCareActivity.class));
                activity.finish();
            }
        });

        navChat.setOnClickListener(v -> {
            if (!(activity instanceof ChatActivity)) {
                activity.startActivity(new Intent(activity, ChatActivity.class));
                activity.finish();
            }
        });
    }
}
