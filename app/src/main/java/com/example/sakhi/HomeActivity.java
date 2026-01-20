package com.example.sakhi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.PopupMenu;
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

        // Profile image click → menu
        imgProfile = findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(v -> showProfileMenu());
    }

    private void showProfileMenu() {
        PopupMenu popupMenu = new PopupMenu(this, imgProfile);
        popupMenu.getMenuInflater().inflate(R.menu.menu_profile, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;

            } else if (id == R.id.menu_reminders) {
                startActivity(new Intent(this, RemainderActivity.class));
                return true;

            } else if (id == R.id.menu_logout) {
                logoutUser();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void logoutUser() {
        // ✅ Clear Supabase session locally
        SessionManager.clearSession(this);

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Go to Login screen & clear back stack
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
