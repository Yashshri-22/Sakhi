package com.example.sakhi;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
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

public class SignupActivity extends AppCompatActivity {

    private static final String SUPABASE_KEY =
            "sb_publishable_KIBycSCIXbC03ppk_GSVFA_cMT6K7fY";

    EditText etEmail, etPassword, etConfirm;
    Button btnRegister;
    ProgressBar progressSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etEmail = findViewById(R.id.etSignupEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirm = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        progressSignup = findViewById(R.id.progressSignup);

        findViewById(R.id.loginText).setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));

        btnRegister.setOnClickListener(v -> attemptSignup());

        EditText etPassword = findViewById(R.id.etPassword);
        ImageView ivToggle = findViewById(R.id.ivTogglePassword);

        ivToggle.setOnClickListener(new View.OnClickListener() {

            boolean isVisible = false;

            @Override
            public void onClick(View v) {

                if (isVisible) {
                    // Hide password
                    etPassword.setInputType(
                            InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_PASSWORD
                    );
                    ivToggle.setImageResource(R.drawable.ic_closed_eye);
                } else {
                    // Show password
                    etPassword.setInputType(
                            InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    );
                    ivToggle.setImageResource(R.drawable.ic_open_eye);
                }

                // Keep cursor at end
                etPassword.setSelection(etPassword.getText().length());
                isVisible = !isVisible;
            }
        });

        EditText etConfirmPassword = findViewById(R.id.etConfirmPassword);
        ImageView ivToggleConfirm = findViewById(R.id.ivTogglePassword1);

        ivToggleConfirm.setOnClickListener(new View.OnClickListener() {

            boolean isVisible = false;

            @Override
            public void onClick(View v) {

                if (isVisible) {
                    etConfirmPassword.setInputType(
                            InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_PASSWORD
                    );
                    ivToggleConfirm.setImageResource(R.drawable.ic_closed_eye);
                } else {
                    etConfirmPassword.setInputType(
                            InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    );
                    ivToggleConfirm.setImageResource(R.drawable.ic_open_eye);
                }

                etConfirmPassword.setSelection(
                        etConfirmPassword.getText().length()
                );

                isVisible = !isVisible;
            }
        });
    }

    private void attemptSignup() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirm = etConfirm.getText().toString().trim();

        // 🔴 VALIDATIONS
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirm)) {
            etConfirm.setError("Passwords do not match");
            etConfirm.requestFocus();
            return;
        }

        // 🔄 SHOW LOADER
        setLoading(true);

        JsonObject user = new JsonObject();
        user.addProperty("email", email);
        user.addProperty("password", password);

        SupabaseApi api =
                RetrofitClient.getClient().create(SupabaseApi.class);

        api.signUp(SUPABASE_KEY, user)
                .enqueue(new Callback<JsonObject>() {

                    @Override
                    public void onResponse(
                            Call<JsonObject> call,
                            Response<JsonObject> response) {

                        setLoading(false);

                        if (response.isSuccessful()) {
                            Toast.makeText(
                                    SignupActivity.this,
                                    "Signup successful! Please login 🌸",
                                    Toast.LENGTH_LONG
                            ).show();

                            startActivity(
                                    new Intent(
                                            SignupActivity.this,
                                            LoginActivity.class
                                    )
                            );
                            finish();
                        } else {
                            Toast.makeText(
                                    SignupActivity.this,
                                    "Signup failed: Email already exists",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<JsonObject> call,
                            Throwable t) {

                        setLoading(false);

                        Toast.makeText(
                                SignupActivity.this,
                                "Network error. Please try again.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void setLoading(boolean loading) {
        progressSignup.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!loading);
        btnRegister.setAlpha(loading ? 0.6f : 1f);
    }
}
