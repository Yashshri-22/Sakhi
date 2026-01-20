package com.example.sakhi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String SUPABASE_KEY = "sb_publishable_KIBycSCIXbC03ppk_GSVFA_cMT6K7fY";

    EditText etEmail, etPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ AUTO-LOGIN CHECK
        if (SessionManager.isLoggedIn(this)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Link to Signup Page
        findViewById(R.id.signupText).setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Prepare Request Body
            JsonObject user = new JsonObject();
            user.addProperty("email", email);
            user.addProperty("password", password);

            // API Call
            SupabaseApi api = RetrofitClient.getClient().create(SupabaseApi.class);
            Call<JsonObject> call = api.login(SUPABASE_KEY, "password", user);

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    if (response.isSuccessful() && response.body() != null) {

                        // ✅ SAVE SESSION HERE
                        String accessToken = response.body()
                                .get("access_token").getAsString();
                        String refreshToken = response.body()
                                .get("refresh_token").getAsString();

                        SessionManager.saveSession(LoginActivity.this, accessToken, refreshToken);

                        Toast.makeText(LoginActivity.this,
                                "Login Successful!", Toast.LENGTH_SHORT).show();

                        // Go to Home
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(LoginActivity.this,
                                "Login Failed. Check email/password.",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Toast.makeText(LoginActivity.this,
                            "Network Error: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
