package com.example.sakhi;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String SUPABASE_KEY =
            "sb_publishable_KIBycSCIXbC03ppk_GSVFA_cMT6K7fY";

    EditText etEmail, etPassword;
    Button btnLogin;
    ImageView ivToggle;
    ProgressBar progressBar;

    boolean isVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ AUTO LOGIN
        if (SessionManager.isLoggedIn(this)) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        ivToggle = findViewById(R.id.ivToggleLoginPassword);
        progressBar = findViewById(R.id.progressLogin);

        /* =======================
           👁 PASSWORD TOGGLE
        ======================== */
        ivToggle.setOnClickListener(v -> {
            if (isVisible) {
                etPassword.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD
                );
                ivToggle.setImageResource(R.drawable.ic_closed_eye);
            } else {
                etPassword.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                );
                ivToggle.setImageResource(R.drawable.ic_open_eye);
            }
            etPassword.setSelection(etPassword.getText().length());
            isVisible = !isVisible;
        });

        /* =======================
           🔐 FORGOT PASSWORD
        ======================== */
        findViewById(R.id.txtForgetPassword).setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this,
                        "Enter your email first",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            JsonObject body = new JsonObject();
            body.addProperty("email", email);
            body.addProperty("redirect_to", "sakhi://reset-password");

            SupabaseApi api =
                    RetrofitClient.getClient().create(SupabaseApi.class);

            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);

            api.resetPassword(SUPABASE_KEY, body)
                    .enqueue(new Callback<Void>() {

                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            progressBar.setVisibility(View.GONE);
                            btnLogin.setEnabled(true);

                            if (response.isSuccessful()) {
                                Toast.makeText(
                                        LoginActivity.this,
                                        "Password reset email sent 📩",
                                        Toast.LENGTH_LONG
                                ).show();
                            } else {
                                Toast.makeText(
                                        LoginActivity.this,
                                        "Failed to send reset email",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                            btnLogin.setEnabled(true);

                            Toast.makeText(
                                    LoginActivity.this,
                                    "Error: " + t.getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });
        });

        /* =======================
           🔑 LOGIN
        ======================== */
        btnLogin.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this,
                        "Enter email and password",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            JsonObject body = new JsonObject();
            body.addProperty("email", email);
            body.addProperty("password", password);

            SupabaseApi api =
                    RetrofitClient.getClient().create(SupabaseApi.class);

            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);

            api.login(SUPABASE_KEY, "password", body)
                    .enqueue(new Callback<JsonObject>() {

                        @Override
                        public void onResponse(
                                Call<JsonObject> call,
                                Response<JsonObject> response) {

                            progressBar.setVisibility(View.GONE);
                            btnLogin.setEnabled(true);

                            if (response.isSuccessful() && response.body() != null) {

                                String accessToken =
                                        response.body().get("access_token").getAsString();

                                String refreshToken =
                                        response.body().get("refresh_token").getAsString();

                                String userId =
                                        response.body()
                                                .getAsJsonObject("user")
                                                .get("id")
                                                .getAsString();

                                SessionManager.saveSession(
                                        LoginActivity.this,
                                        accessToken,
                                        refreshToken
                                );
                                SessionManager.saveUserId(
                                        LoginActivity.this,
                                        userId
                                );

                                // ✅ CLEAR BACK STACK
                                Intent intent =
                                        new Intent(LoginActivity.this, HomeActivity.class);
                                intent.setFlags(
                                        Intent.FLAG_ACTIVITY_NEW_TASK |
                                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                                );
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(
                                        LoginActivity.this,
                                        "Login failed. Check credentials.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                            btnLogin.setEnabled(true);

                            Toast.makeText(
                                    LoginActivity.this,
                                    "Network error: " + t.getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });
        });

        /* =======================
           🔗 SIGNUP LINK
        ======================== */
        findViewById(R.id.signupText).setOnClickListener(v ->
                startActivity(new Intent(this, SignupActivity.class))
        );
    }
}
