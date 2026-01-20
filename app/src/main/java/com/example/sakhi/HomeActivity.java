package com.example.sakhi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;

public class HomeActivity extends AppCompatActivity {

    private ShapeableImageView imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Bottom navigation
        BottomNavHelper.setupBottomNav(this, R.id.navHome);

        // Profile image click → custom menu
        imgProfile = findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(v -> showProfileMenu());
    }

    private void showProfileMenu() {

        View menuView = getLayoutInflater()
                .inflate(R.layout.layout_profile_menu, null);

        PopupWindow popupWindow = new PopupWindow(
                menuView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
        );

        popupWindow.setElevation(20f);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        // Show menu near profile image (adjust if needed)
        popupWindow.showAsDropDown(imgProfile, -90, 20);

        // Profile
        menuView.findViewById(R.id.menuProfile).setOnClickListener(v -> {
            popupWindow.dismiss();
            startActivity(new Intent(this, ProfileActivity.class));
        });

        // Reminders
        menuView.findViewById(R.id.menuReminders).setOnClickListener(v -> {
            popupWindow.dismiss();
            startActivity(new Intent(this, RemainderActivity.class));
        });

        // Logout
        menuView.findViewById(R.id.menuLogout).setOnClickListener(v -> {
            popupWindow.dismiss();
            logoutUser();
        });
    }

    private void logoutUser() {
        // Clear Supabase session
        SessionManager.clearSession(this);

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
