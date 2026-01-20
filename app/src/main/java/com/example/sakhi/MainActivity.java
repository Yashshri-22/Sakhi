package com.example.sakhi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔐 AUTO-LOGIN CHECK (FIRST THING)
        if (SessionManager.isLoggedIn(this)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        // Edge-to-edge UI
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        WindowInsetsControllerCompat controller =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);
        controller.setAppearanceLightNavigationBars(true);

        setContentView(R.layout.activity_main);

        // 🔹 Buttons
        MaterialButton btnLogin = findViewById(R.id.btn_login);
        MaterialButton btnSignup = findViewById(R.id.btn_signup);

        // Login click
        btnLogin.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, LoginActivity.class))
        );

        // Signup click
        btnSignup.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SignupActivity.class))
        );
    }
}
