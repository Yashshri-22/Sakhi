package com.example.sakhi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.sakhi.BottomNavHelper;


public class PeriodCalendarActivity extends AppCompatActivity {

    private static final String SUPABASE_KEY =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InNic3Bxbm5tdWxsZXpscGJkemhzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Njg4MTc4NDYsImV4cCI6MjA4NDM5Mzg0Nn0.H9p0LoBRWEgjKBRSfKg1DdwnCN7qV2dQCo2gVEL7DiU";

    RecyclerView rv;
    TextView tvNext, tvMonth;
    ImageView btnPrev, btnNext;

    Calendar currentMonth = Calendar.getInstance();

    // 🔴 Cycle data from Supabase
    Date lastPeriod;
    int cycleLength;
    int periodLength;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_period_calendar);

        rv = findViewById(R.id.calendarRecycler);
        tvNext = findViewById(R.id.tvNextPeriod);
        tvMonth = findViewById(R.id.tvMonth);
        btnPrev = findViewById(R.id.btnPrevMonth);
        btnNext = findViewById(R.id.btnNextMonth);
        // Bottom navigation (Menstrual / Calendar tab active)
        BottomNavHelper.setupBottomNav(this, R.id.navPeriod);

        rv.setLayoutManager(new GridLayoutManager(this, 7));

        findViewById(R.id.btnEditCycle)
                .setOnClickListener(v ->
                        startActivity(new Intent(this, EditCycleActivity.class)));

        btnPrev.setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, -1);
            renderCalendar();
        });

        btnNext.setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, 1);
            renderCalendar();
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
            overridePendingTransition(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
            );
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCycleFromSupabase();
    }

    private void loadCycleFromSupabase() {

        String userId = SessionManager.getUserId(this);
        String token = SessionManager.getAccessToken(this);

        if (userId == null || token == null) return;

        SupabasePeriodApi api =
                RetrofitClient.getClient().create(SupabasePeriodApi.class);

        api.getCycle(
                SUPABASE_KEY,
                "Bearer " + token,
                "eq." + userId
        ).enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> res) {

                if (!res.isSuccessful() || res.body() == null || res.body().size() == 0)
                    return;

                try {
                    JsonObject data = res.body().get(0).getAsJsonObject();

                    SimpleDateFormat sdf =
                            new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    sdf.setLenient(false);

                    lastPeriod =
                            sdf.parse(data.get("last_period_date").getAsString());
                    cycleLength = data.get("cycle_length").getAsInt();
                    periodLength = data.get("period_length").getAsInt();

                    renderCalendar();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void renderCalendar() {

        if (lastPeriod == null) return;

        List<CalendarDay> cells = new ArrayList<>();

        Calendar cal = (Calendar) currentMonth.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);

        tvMonth.setText(
                new SimpleDateFormat("MMMM yyyy", Locale.US)
                        .format(cal.getTime())
        );

        int offset = cal.get(Calendar.DAY_OF_WEEK) - 1;
        for (int i = 0; i < offset; i++) {
            CalendarDay empty = new CalendarDay();
            empty.date = null;
            cells.add(empty);
        }

        Date ovulation =
                CycleCalculator.getOvulation(lastPeriod, cycleLength);
        List<Date> fertile =
                CycleCalculator.getFertileWindow(ovulation);
        List<Date> period =
                CycleCalculator.getPeriodDays(lastPeriod, periodLength);

        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < daysInMonth; i++) {
            CalendarDay d = new CalendarDay();
            d.date = cal.getTime();
            d.isToday = same(d.date, new Date());
            d.isPeriod = contains(period, d.date);
            d.isOvulation = same(d.date, ovulation);
            d.isFertile = contains(fertile, d.date);
            cells.add(d);
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        rv.setAdapter(new CalendarAdapter(cells));

        Date next =
                CycleCalculator.getNextPeriod(lastPeriod, cycleLength);
        tvNext.setText(
                "Next Period: " +
                        new SimpleDateFormat("dd MMM", Locale.US)
                                .format(next)
        );
    }

    private boolean same(Date a, Date b) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(a);
        c2.setTime(b);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR)
                == c2.get(Calendar.DAY_OF_YEAR);
    }

    private boolean contains(List<Date> list, Date d) {
        for (Date x : list) if (same(x, d)) return true;
        return false;
    }
}
