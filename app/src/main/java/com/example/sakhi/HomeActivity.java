package com.example.sakhi;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class HomeActivity extends AppCompatActivity {

    LinearLayout navHome, navSurvey, navNearby, navChat;
    ImageView iconHome, iconSurvey, iconNearby, iconChat;
    TextView textHome, textSurvey, textNearby, textChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavHelper.setupBottomNav(this, R.id.navHome);
    }
}
