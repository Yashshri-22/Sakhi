package com.example.sakhi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SymptomChatActivity extends AppCompatActivity {

    // ▼▼▼ USE 1.5-FLASH (2.5 DOES NOT EXIST) ▼▼▼
    private static final String API_KEY = "AIzaSyCha9Bcd6vysEQeaJVgnEbWE9jlTKB4Jag";

    TextView tvAIQuestion, tvTitle;
    EditText etMessage;
    ImageButton btnSend;
    ProgressBar progressBar;

    GenerativeModelFutures model;
    StringBuilder conversationHistory = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_chat);

        // Initialize Views
        tvTitle = findViewById(R.id.tvTitle);
        tvAIQuestion = findViewById(R.id.tvAIQuestion);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        progressBar = findViewById(R.id.progressBar);

        // Setup AI Model (gemini-1.5-flash is currently the standard fast model)
        GenerativeModel gm = new GenerativeModel("gemini-2.5-flash", API_KEY);
        model = GenerativeModelFutures.from(gm);

        btnSend.setOnClickListener(v -> sendMessage());
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            onBackPressed();
            overridePendingTransition(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
            );
        });
    }

    @Override
    public void onBackPressed() {
        if (!isTaskRoot()) {
            super.onBackPressed(); // go to previous screen
        } else {
            // No previous screen → go to Home
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }

    private void sendMessage() {
        String userMsg = etMessage.getText().toString().trim();
        if (userMsg.isEmpty()) return;

        // 1. Update UI to show loading
        etMessage.setText("");
        progressBar.setVisibility(View.VISIBLE);
        tvAIQuestion.setText("Analyzing your symptoms...");
        tvTitle.setText("Thinking...");

        // 2. Add to history
        conversationHistory.append("User: ").append(userMsg).append("\n");

        // --- DOCTOR PROMPT ---
        String systemPrompt = "You are a medical triage assistant for women.\n" +
                "Conversation History:\n" + conversationHistory.toString() + "\n" +
                "Instructions:\n" +
                "1. Ask clarification questions if needed (one at a time).\n" +
                "2. If you suspect a specific condition (e.g., PCOS, Anemia, Migraine, Thyroid, etc.), do NOT diagnose immediately.\n" +
                "3. Instead, output this specific code: [POSSIBLE: Condition Name]\n" +
                "4. Example: '[POSSIBLE: Thyroid]'\n" +
                "5. If not sure yet, just reply normally.";

        // 4. Call AI
        Content content = new Content.Builder().addText(systemPrompt).build();
        Executor executor = Executors.newSingleThreadExecutor();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String aiReply = result.getText().trim();
                conversationHistory.append("AI: ").append(aiReply).append("\n");

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);

                    if (aiReply.contains("[POSSIBLE:")) {
                        // --- SUCCESS: Condition Found ---
                        String condition = aiReply.substring(aiReply.indexOf(":") + 1, aiReply.indexOf("]")).trim();

                        // Navigate to Survey
                        Intent intent = new Intent(SymptomChatActivity.this, SymptomSurveyActivity.class);
                        intent.putExtra("CONDITION_NAME", condition);
                        startActivity(intent);
                        finish();
                    } else {
                        // --- NEED MORE INFO: Update the Card ---
                        tvTitle.setText("Follow Up");
                        tvAIQuestion.setText(aiReply); // Replaces the old text with the new question
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    tvTitle.setText("Error");
                    tvAIQuestion.setText("Something went wrong. Please try again.");
                    Toast.makeText(SymptomChatActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }, executor);
    }
}
