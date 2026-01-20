package com.example.sakhi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RemainderActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RemainderAdapter adapter;
    List<RemainderModel> reminderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remainder); // Your list XML

        recyclerView = findViewById(R.id.rvReminders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Button to open "Add Reminder" page
        Button btnAdd = findViewById(R.id.btnAddReminder);
        ImageView btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> {
            finish(); // standard Android back behavior
        });

        btnAdd.setOnClickListener(v -> {
            startActivity(new Intent(RemainderActivity.this, AddRemainderActivity.class));
        });

        loadReminders();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload list when coming back from "Add" page
        loadReminders();
    }

    private void loadReminders() {
        SharedPreferences prefs = getSharedPreferences("SakhiData", MODE_PRIVATE);
        String json = prefs.getString("reminders", null);

        reminderList = new ArrayList<>();
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<RemainderModel>>() {}.getType();
            reminderList = gson.fromJson(json, type);
        }

        // Add some dummy data if empty (Optional)
        if (reminderList.isEmpty()) {
            reminderList.add(new RemainderModel("Health Check", "10:00 AM", "Monthly", true));
        }

        adapter = new RemainderAdapter(reminderList);
        recyclerView.setAdapter(adapter);
    }
}

