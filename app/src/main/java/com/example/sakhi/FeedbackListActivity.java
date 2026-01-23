package com.example.sakhi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sakhi.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FeedbackListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FeedbackAdapter adapter;
    ArrayList<JSONObject> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_list);

        // 1. Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerFeedback);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FeedbackAdapter(list);
        recyclerView.setAdapter(adapter);

        // 2. Setup Back Button (In the pink header)
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
            );
        });

        // 3. Setup Share Button (At the bottom)
        Button btnShare = findViewById(R.id.btnShare);
        btnShare.setOnClickListener(v -> {
            Intent intent = new Intent(FeedbackListActivity.this, AddFeedbackActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data every time this screen appears
        loadFeedback();
    }

    private void loadFeedback() {
        list.clear();
        SharedPreferences prefs = getSharedPreferences("sakhi_prefs", MODE_PRIVATE);
        // We use JSON format now, not "StringSet"
        String data = prefs.getString("feedback_data", "[]");

        try {
            JSONArray array = new JSONArray(data);
            for (int i = 0; i < array.length(); i++) {
                list.add(array.getJSONObject(i));
            }
            // Reverse list so newest feedback shows at the top
            java.util.Collections.reverse(list);
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
