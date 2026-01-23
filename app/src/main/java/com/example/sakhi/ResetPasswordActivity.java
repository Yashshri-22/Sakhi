package com.example.sakhi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private static final String SUPABASE_KEY =
            "sb_publishable_KIBycSCIXbC03ppk_GSVFA_cMT6K7fY";

    EditText etNewPassword, etConfirmPassword;
    Button btnReset;
    ProgressBar progressBar;

    String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnReset = findViewById(R.id.btnReset);
        progressBar = findViewById(R.id.progressReset);

        extractToken();

        btnReset.setOnClickListener(v -> resetPassword());
    }

    private void extractToken() {
        Uri uri = getIntent().getData();

        if (uri == null || uri.getFragment() == null) {
            Toast.makeText(this, "Invalid reset link", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        for (String part : uri.getFragment().split("&")) {
            if (part.startsWith("access_token=")) {
                accessToken = part.replace("access_token=", "");
            }
        }

        if (accessToken == null) {
            Toast.makeText(this, "Token missing", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void resetPassword() {

        String pass = etNewPassword.getText().toString().trim();
        String confirm = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(pass) || pass.length() < 6) {
            Toast.makeText(this, "Min 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(confirm)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObject body = new JsonObject();
        body.addProperty("password", pass);

        SupabaseApi api =
                RetrofitClient.getClient().create(SupabaseApi.class);

        progressBar.setVisibility(View.VISIBLE);
        btnReset.setEnabled(false);

        api.updatePassword(
                SUPABASE_KEY,
                "Bearer " + accessToken,
                "application/json",
                body
        ).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                btnReset.setEnabled(true);

                if (response.isSuccessful()) {
                    Toast.makeText(
                            ResetPasswordActivity.this,
                            "Password reset successful",
                            Toast.LENGTH_LONG
                    ).show();

                    startActivity(
                            new Intent(
                                    ResetPasswordActivity.this,
                                    LoginActivity.class
                            )
                    );
                    finish();
                } else {
                    Toast.makeText(
                            ResetPasswordActivity.this,
                            "Reset failed (" + response.code() + ")",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnReset.setEnabled(true);
                Toast.makeText(
                        ResetPasswordActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}
