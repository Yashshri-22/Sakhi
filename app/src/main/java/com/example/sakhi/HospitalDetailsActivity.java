package com.example.sakhi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class HospitalDetailsActivity extends AppCompatActivity {

    private TextView desc;
    private String hospitalName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_details);

        // 1. Initialize Views
        ImageView img = findViewById(R.id.detailImage);
        TextView title = findViewById(R.id.detailTitle);
        TextView loc = findViewById(R.id.detailLocation);
        desc = findViewById(R.id.detailDescription);
        ImageView verified = findViewById(R.id.detailVerified);
        Button btnDirection = findViewById(R.id.btnDirection);

        // 2. Get Data passed from MainActivity
        hospitalName = getIntent().getStringExtra("NAME");
        String locData = getIntent().getStringExtra("LOCATION");
        boolean isVerified = getIntent().getBooleanExtra("VERIFIED", false);

        // 3. Set Basic Data
        title.setText(hospitalName);
        loc.setText(locData);

        if (isVerified) {
            verified.setVisibility(View.VISIBLE);
        } else {
            verified.setVisibility(View.GONE);
        }

        // 4. FETCH REAL-TIME DATA (Updated Logic)
        fetchWikipediaDetails(hospitalName);

        // 5. Direction Button Logic
        btnDirection.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(hospitalName + " " + locData));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            try {
                startActivity(mapIntent);
            } catch (Exception e) {
                Toast.makeText(this, "Opening in browser...", Toast.LENGTH_SHORT).show();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + Uri.encode(hospitalName)));
                startActivity(browserIntent);
            }
        });
    }

    // --- Helper Method: Fetch Wiki Data with Smart Search ---
    private void fetchWikipediaDetails(String query) {
        // Set loading text
        runOnUiThread(() -> desc.setText("Searching Wikipedia for info..."));

        new Thread(() -> {
            try {
                // 1. Prepare Smart URL
                // We use 'generator=search' to find the closest match, not just exact title
                String encodedName = URLEncoder.encode(query + " hospital", "UTF-8");
                String urlString = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro&explaintext&generator=search&gsrlimit=1&gsrsearch=" + encodedName;

                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(5000);

                // 2. Read Response
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // 3. Parse JSON
                JSONObject jsonResponse = new JSONObject(response.toString());

                String resultText = "No specific information found on Wikipedia for this hospital.";

                if (jsonResponse.has("query")) {
                    JSONObject queryObj = jsonResponse.getJSONObject("query");
                    if (queryObj.has("pages")) {
                        JSONObject pages = queryObj.getJSONObject("pages");
                        Iterator<String> keys = pages.keys();
                        if (keys.hasNext()) {
                            String pageId = keys.next();
                            JSONObject page = pages.getJSONObject(pageId);
                            if (page.has("extract")) {
                                resultText = page.getString("extract");

                                // Clean up empty results
                                if (resultText.trim().isEmpty()) {
                                    resultText = "No detailed description available on Wikipedia.";
                                }
                            }
                        }
                    }
                }

                // 4. Update UI
                String finalResult = resultText;
                runOnUiThread(() -> desc.setText(finalResult));

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("WikiError", "Error fetching data: " + e.getMessage());
                runOnUiThread(() -> desc.setText("Could not fetch details. (Requires Internet)"));
            }
        }).start();
    }
}

