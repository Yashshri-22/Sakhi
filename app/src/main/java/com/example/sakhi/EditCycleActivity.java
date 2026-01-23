package com.example.sakhi;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditCycleActivity extends AppCompatActivity {

    private static final String SUPABASE_KEY =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InNic3Bxbm5tdWxsZXpscGJkemhzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Njg4MTc4NDYsImV4cCI6MjA4NDM5Mzg0Nn0.H9p0LoBRWEgjKBRSfKg1DdwnCN7qV2dQCo2gVEL7DiU";

    EditText etLast, etCycle, etPeriod;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_edit_cycle);

        etLast = findViewById(R.id.etLastPeriod);
        etCycle = findViewById(R.id.etCycleLength);
        etPeriod = findViewById(R.id.etPeriodLength);

        // ✅ DatePicker instead of typing
        etLast.setOnClickListener(v -> showDatePicker());

        findViewById(R.id.btnSave).setOnClickListener(v -> saveCycle());
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
            overridePendingTransition(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
            );
        });
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();

        new DatePickerDialog(
                this,
                (view, y, m, d) -> {
                    Calendar c = Calendar.getInstance();
                    c.set(y, m, d);
                    etLast.setText(
                            new SimpleDateFormat("yyyy-MM-dd", Locale.US)
                                    .format(c.getTime())
                    );
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void saveCycle() {

        String userId = SessionManager.getUserId(this);
        String token = SessionManager.getAccessToken(this);

        if (userId == null || token == null) {
            Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObject body = new JsonObject();
        body.addProperty("user_id", userId);
        body.addProperty("last_period_date", etLast.getText().toString());
        body.addProperty("cycle_length",
                Integer.parseInt(etCycle.getText().toString()));
        body.addProperty("period_length",
                Integer.parseInt(etPeriod.getText().toString()));

        SupabasePeriodApi api =
                RetrofitClient.getClient().create(SupabasePeriodApi.class);

        api.saveCycle(
                SUPABASE_KEY,
                "Bearer " + token,
                "resolution=merge-duplicates",
                body
        ).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(
                        EditCycleActivity.this,
                        "Cycle saved successfully",
                        Toast.LENGTH_SHORT
                ).show();
                finish(); // 🔁 return to calendar
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(
                        EditCycleActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}
