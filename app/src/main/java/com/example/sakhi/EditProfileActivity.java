package com.example.sakhi;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InNic3Bxbm5tdWxsZXpscGJkemhzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Njg4MTc4NDYsImV4cCI6MjA4NDM5Mzg0Nn0.H9p0LoBRWEgjKBRSfKg1DdwnCN7qV2dQCo2gVEL7DiU";

    EditText etName, etAge, etHeight, etWeight, etCondition;
    MaterialButton btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        etCondition = findViewById(R.id.etCondition);

        btnSave = findViewById(R.id.btnSave);
        findViewById(R.id.btnCancel).setOnClickListener(v -> {
            finish();
            overridePendingTransition(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
            );
        });
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {

        String userId = SessionManager.getUserId(this);
        String accessToken = SessionManager.getAccessToken(this);

        if (userId == null || accessToken == null) {
            Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show();
            return;
        }

        if (etName.getText().toString().isEmpty()
                || etAge.getText().toString().isEmpty()
                || etHeight.getText().toString().isEmpty()
                || etWeight.getText().toString().isEmpty()) {

            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObject body = new JsonObject();
        body.addProperty("id", userId);
        body.addProperty("full_name", etName.getText().toString());
        body.addProperty("age", Integer.parseInt(etAge.getText().toString()));
        body.addProperty("height_cm", Integer.parseInt(etHeight.getText().toString()));
        body.addProperty("weight_kg", Integer.parseInt(etWeight.getText().toString()));
        body.addProperty("conditions", etCondition.getText().toString());

        SupabaseProfileApi api =
                RetrofitClient.getClient().create(SupabaseProfileApi.class);

        api.saveProfile(
                SUPABASE_KEY,
                "Bearer " + accessToken,
                "application/json",
                "resolution=merge-duplicates",
                body
        ).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this,
                            "Profile saved successfully",
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    try {
                        String error = response.errorBody().string();
                        Toast.makeText(EditProfileActivity.this,
                                error,
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(EditProfileActivity.this,
                                "Unknown error",
                                Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
