package com.example.sakhi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InNic3Bxbm5tdWxsZXpscGJkemhzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Njg4MTc4NDYsImV4cCI6MjA4NDM5Mzg0Nn0.H9p0LoBRWEgjKBRSfKg1DdwnCN7qV2dQCo2gVEL7DiU";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        findViewById(R.id.btnEditProfile).setOnClickListener(v ->
                startActivity(new Intent(this, EditProfileActivity.class))
        );
    }

    // 🔹 THIS IS THE KEY FIX
    @Override
    protected void onResume() {
        super.onResume();
        loadProfile();
    }

    private void loadProfile() {

        String userId = SessionManager.getUserId(this);
        String accessToken = SessionManager.getAccessToken(this);

        if (userId == null || accessToken == null) {
            Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show();
            return;
        }

        SupabaseProfileApi api =
                RetrofitClient.getClient().create(SupabaseProfileApi.class);

        api.getProfile(
                SUPABASE_KEY,
                "Bearer " + accessToken,
                "eq." + userId,
                "*"
        ).enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().size() > 0) {

                    JsonObject profile = response.body().get(0).getAsJsonObject();

                    setField(R.id.fieldName, "Name", get(profile, "full_name"));
                    setField(R.id.fieldAge, "Age", get(profile, "age"));
                    setField(R.id.fieldHeight, "Height", get(profile, "height_cm") + " cm");
                    setField(R.id.fieldWeight, "Weight", get(profile, "weight_kg") + " kg");
                    setField(R.id.fieldCycle, "Cycle Length",
                            get(profile, "menstrual_cycle_length") + " days");
                    setField(R.id.fieldLastPeriod, "Last Period Date",
                            get(profile, "last_period_date"));
                    setField(R.id.fieldCondition, "Condition",
                            get(profile, "conditions"));

                } else {
                    Toast.makeText(ProfileActivity.this,
                            "Profile not found",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(ProfileActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setField(int fieldId, String label, String value) {
        View field = findViewById(fieldId);
        TextView tvLabel = field.findViewById(R.id.tvLabel);
        TextView tvValue = field.findViewById(R.id.tvValue);

        tvLabel.setText(label);
        tvValue.setText(value == null || value.equals("null") ? "Not set" : value);
    }

    private String get(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull()
                ? obj.get(key).getAsString()
                : "Not set";
    }
}
