package com.example.sakhi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SymptomSurveyActivity extends AppCompatActivity {

    // ▼▼▼ PASTE YOUR KEY HERE ▼▼▼
    private static final String API_KEY = "AIzaSyBqJTgeLli1JyQvA5QImtbHBCsrzZc6pXQ";

    TextView tvCondition;
    LinearLayout questionsContainer;
    Button btnAnalyze;
    ProgressBar progressBar;
    String conditionName;
    List<CheckBox> checkBoxes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_survey);

        tvCondition = findViewById(R.id.tvSuspectedCondition);
        questionsContainer = findViewById(R.id.questionsContainer);
        btnAnalyze = findViewById(R.id.btnAnalyze);
        progressBar = findViewById(R.id.progressBar);

        // 1. Get the suspected condition
        conditionName = getIntent().getStringExtra("CONDITION_NAME");
        if (conditionName == null) conditionName = "Unknown Issue";

        tvCondition.setText("Checking for: " + conditionName);

        // 2. Generate Questions using AI
        generateQuestions(conditionName);

        // 3. Analyze Results
        btnAnalyze.setOnClickListener(v -> analyzeResults());

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            onBackPressed();
            overridePendingTransition(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
            );
        });
    }

    public void onBackPressed() {
        if (!isTaskRoot()) {
            super.onBackPressed(); // go to previous screen
        } else {
            // No previous screen → go to Home
            startActivity(new Intent(this, SymptomChatActivity.class));
            finish();
        }
    }

    private void generateQuestions(String condition) {
        GenerativeModel gm = new GenerativeModel("gemini-2.5-flash", API_KEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        // Smart Prompt: Ask AI for exactly 3 questions separated by pipes (|)
        String prompt = "Generate exactly 3 simple Yes/No diagnostic questions to confirm if a patient has " + condition + ". " +
                "Return ONLY the questions separated by a pipe symbol (|). " +
                "Example: Do you feel dizzy?|Is your skin pale?|Do you have cold hands?";

        Content content = new Content.Builder().addText(prompt).build();
        Executor executor = Executors.newSingleThreadExecutor();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String rawText = result.getText().trim();
                // Split the AI response into 3 questions
                String[] questions = rawText.split("\\|");

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);

                    // Create a Checkbox for each question
                    for (String q : questions) {
                        CheckBox cb = new CheckBox(SymptomSurveyActivity.this);
                        cb.setText(q.trim());
                        cb.setTextSize(16f);
                        cb.setPadding(0, 20, 0, 20);
                        cb.setTextColor(Color.BLACK);
                        questionsContainer.addView(cb);
                        checkBoxes.add(cb);
                    }
                    btnAnalyze.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> Toast.makeText(SymptomSurveyActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }, executor);
    }

    private void analyzeResults() {
        int yesCount = 0;
        for (CheckBox cb : checkBoxes) {
            if (cb.isChecked()) yesCount++;
        }

        // Logic: If they said YES to at least 2 questions, we confirm it.
        Intent intent = new Intent(SymptomSurveyActivity.this, SymptomSummaryActivity.class);

        if (yesCount >= 2) {
            // Confirmed!
            intent.putExtra("CONDITION_NAME", conditionName);
        } else {
            // Not sure
            intent.putExtra("CONDITION_NAME", "General Health Issue");
        }

        startActivity(intent);
        finish();
    }
}
