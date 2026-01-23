package com.example.sakhi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SymptomSummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_summary); // Your 2nd XML file name

        // 1. Get the Diagnosis from the Chat
        String condition = getIntent().getStringExtra("CONDITION_NAME");
        if (condition == null) condition = "General Checkup";

        // 2. Update the Text on the Card
        TextView tvConditionTitle = findViewById(R.id.tvConditionName);
        if(tvConditionTitle != null) {
            tvConditionTitle.setText(condition);
        }

        // 3. Update the description text (Optional)
        TextView tvMessage = findViewById(R.id.tvMessage);
        tvMessage.setText("Your symptoms suggest a possibility of " + condition + ".");

        // 4. "Find Nearby Care" Button Logic
        Button btnFindCare = findViewById(R.id.btnFindCare);
        String finalCondition = condition;

        btnFindCare.setOnClickListener(v -> {
            // Create a Google Maps Search Query
            String query = "Doctors for " + finalCondition + " near me";

            // Open Maps App
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(query));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            // Check if Maps is installed, otherwise open browser
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                // Fallback to Browser
                Uri webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(query));
                startActivity(new Intent(Intent.ACTION_VIEW, webUri));
            }
        });

        // Back Button
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
            startActivity(new Intent(this, SymptomChatActivity.class));
            finish();
        }
    }

}
