package com.example.sakhi;

import android.content.Intent; // Imported Intent
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.sakhi.R;
import org.json.JSONArray;
import org.json.JSONObject;

public class AddFeedbackActivity extends AppCompatActivity {

    EditText etFeedback;
    RatingBar ratingBar;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feedback);

        // 1. Connect Views
        etFeedback = findViewById(R.id.etFeedbackInput);
        ratingBar = findViewById(R.id.ratingBarInput);
        btnSubmit = findViewById(R.id.btnSubmitFeedback);
        ImageView btnBack = findViewById(R.id.btnBackAdd);

        // 2. Back Button logic
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
            );
        });


        // 3. Submit Button logic
        btnSubmit.setOnClickListener(v -> saveFeedback());
    }

    private void saveFeedback() {
        String text = etFeedback.getText().toString().trim();
        float rating = ratingBar.getRating();

        if (text.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            SharedPreferences prefs = getSharedPreferences("sakhi_prefs", MODE_PRIVATE);
            String oldData = prefs.getString("feedback_data", "[]");
            JSONArray array = new JSONArray(oldData);

            JSONObject newFeedback = new JSONObject();
            newFeedback.put("text", text);
            newFeedback.put("rating", rating);

            array.put(newFeedback);
            prefs.edit().putString("feedback_data", array.toString()).apply();

            Toast.makeText(this, "Feedback Sent!", Toast.LENGTH_SHORT).show();

            // --- CHANGED CODE STARTS HERE ---
            // Instead of just finish(), we explicitly open the list screen
            Intent intent = new Intent(AddFeedbackActivity.this, FeedbackListActivity.class);
            // This flag ensures we don't create multiple copies of the list screen
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            // --- CHANGED CODE ENDS HERE ---

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving feedback", Toast.LENGTH_SHORT).show();
        }
    }
}
