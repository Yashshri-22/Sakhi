package com.example.sakhi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class HospitalDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_details);

        // 1. Initialize Views
        ImageView img = findViewById(R.id.detailImage);
        TextView title = findViewById(R.id.detailTitle);
        TextView loc = findViewById(R.id.detailLocation);
        TextView desc = findViewById(R.id.detailDescription);
        ImageView verified = findViewById(R.id.detailVerified);

        // 2. Get Data passed from MainActivity
        String nameData = getIntent().getStringExtra("NAME");
        String locData = getIntent().getStringExtra("LOCATION");
        String descData = getIntent().getStringExtra("DESC");
        boolean isVerified = getIntent().getBooleanExtra("VERIFIED", false);

        // 3. Set Data
        title.setText(nameData);
        loc.setText(locData);

        // Just show the description/address we passed from the main list
        if (descData != null && !descData.isEmpty()) {
            desc.setText(descData);
        } else {
            desc.setText("No details available for this location.");
        }

        // 4. Show/Hide Verified Badge
        if (isVerified) {
            verified.setVisibility(View.VISIBLE);
        } else {
            verified.setVisibility(View.GONE);
        }

        // 5. Map Click Logic
        loc.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + nameData);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            try {
                startActivity(mapIntent);
            } catch (Exception e) {
                Toast.makeText(this, "Maps not installed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
