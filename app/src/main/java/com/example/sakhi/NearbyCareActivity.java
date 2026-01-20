package com.example.sakhi;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NearbyCareActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    HospitalAdapter adapter;
    List<HospitalModel> fullList;
    EditText searchBar;
    TextView chipAll, chipGynac, chipGeneral, chip247;
    FusedLocationProviderClient fusedLocationClient;
    Location userCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_care);
        BottomNavHelper.setupBottomNav(this, R.id.navNearby);
        // 1. Initialize Views
        recyclerView = findViewById(R.id.recyclerViewHospitals);
        searchBar = findViewById(R.id.etSearch);
        chipAll = findViewById(R.id.chipAll);
        chipGynac = findViewById(R.id.chipGynac);
        chipGeneral = findViewById(R.id.chipGeneral);
        chip247 = findViewById(R.id.chip247);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fullList = new ArrayList<>();

        // Load Demo Data first (so screen isn't empty)
        loadDemoData();

        adapter = new HospitalAdapter(this, fullList);
        recyclerView.setAdapter(adapter);

        // 2. Get Real Location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation();

        // 3. Search Logic
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filterList(s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });

        // 4. Chip Listeners (Fixed Logic)
        chipAll.setOnClickListener(v -> {
            updateChipUI(chipAll);
            adapter.setFilteredList(fullList); // Show everything
        });

        chipGynac.setOnClickListener(v -> {
            updateChipUI(chipGynac);
            filterByCategory("Gynecologist");
        });

        chipGeneral.setOnClickListener(v -> {
            updateChipUI(chipGeneral);
            filterByCategory("General");
        });

        chip247.setOnClickListener(v -> {
            updateChipUI(chip247);
            filterBy247();
        });

        // 5. Map Click Listener
        if (findViewById(R.id.mapCard) != null) {
            findViewById(R.id.mapCard).setOnClickListener(v -> {
                String uri = "geo:0,0?q=hospitals+near+me";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                try { startActivity(intent); }
                catch (Exception e) { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/"))); }
            });
        }
    }

    private void loadDemoData() {
        fullList.add(new HospitalModel("City Care Hospital", "2.5 km • Pimpri", true, "General", "A trusted general hospital providing 24/7 emergency care.", R.drawable.map_image));
        fullList.add(new HospitalModel("Sakhi Maternity Home", "3.0 km • Nigdi", true, "Gynecologist", "Specialized maternity care for women.", R.drawable.map_image));
        fullList.add(new HospitalModel("Dr. Anita's Clinic", "1.2 km • Akurdi", false, "Gynecologist", "Private clinic focusing on women's health.", R.drawable.map_image));
        fullList.add(new HospitalModel("Aditya Birla Hospital", "4.0 km • Chinchwad", true, "General", "Multi-specialty hospital with trauma center.", R.drawable.map_image));
    }

    private void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    100
            );
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        userCurrentLocation = location;

                        String query =
                                "[out:json];node[\"amenity\"~\"hospital|clinic|doctors\"]" +
                                        "(around:5000," + location.getLatitude() + "," +
                                        location.getLongitude() + ");out;";

                        try {
                            String url = "https://overpass-api.de/api/interpreter?data=" +
                                    java.net.URLEncoder.encode(query, "UTF-8");
                            new FetchPlacesTask().execute(url);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    private class FetchPlacesTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                return sb.toString();
            } catch (Exception e) { return null; }
        }
        @Override
        protected void onPostExecute(String result) {
            if (result != null) parseOSMJson(result);
        }
    }

    private void parseOSMJson(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray elements = jsonObject.getJSONArray("elements");

            if (elements.length() > 0) {
                fullList.clear(); // Found real data, remove demo data
                Toast.makeText(NearbyCareActivity.this, "Found " + elements.length() + " places nearby", Toast.LENGTH_SHORT).show();
            }

            for (int i = 0; i < elements.length(); i++) {
                JSONObject place = elements.getJSONObject(i);
                JSONObject tags = place.optJSONObject("tags");

                if (tags != null && tags.has("name")) {
                    String name = tags.getString("name");
                    double lat = place.getDouble("lat");
                    double lon = place.getDouble("lon");

                    // 1. Calculate Distance
                    String distanceStr = "Unknown dist";
                    if (userCurrentLocation != null) {
                        float[] results = new float[1];
                        Location.distanceBetween(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude(), lat, lon, results);
                        distanceStr = String.format("%.1f km", results[0] / 1000);
                    }

                    // 2. SMART TYPE DETECTION (Broadened for better results)
                    String type = "General";
                    String lowerName = name.toLowerCase();

                    if (lowerName.contains("women") || lowerName.contains("maternity") ||
                            lowerName.contains("fertility") || lowerName.contains("mother") ||
                            lowerName.contains("baby") || lowerName.contains("nursing") ||
                            lowerName.contains("clinic") || lowerName.contains("lady")) {
                        // We count generic "Clinics" as Gynecologist for this demo to ensure results show up
                        type = "Gynecologist";
                    }

                    boolean isVerified = tags.has("phone");
                    String desc = "Real-time info from OpenStreetMap.\nAddress: " + tags.optString("addr:full", "See map");

                    fullList.add(new HospitalModel(name, distanceStr, isVerified, type, desc, R.drawable.map_image));
                }
            }
            adapter.setFilteredList(fullList);

        } catch (Exception e) { e.printStackTrace(); }
    }

    private void filterByCategory(String category) {
        List<HospitalModel> filteredList = new ArrayList<>();
        boolean found = false;

        for (HospitalModel item : fullList) {
            if (item.getType().equalsIgnoreCase(category)) {
                filteredList.add(item);
                found = true;
            }
        }

        if (!found) {
            Toast.makeText(this, "No " + category + " found nearby.", Toast.LENGTH_SHORT).show();
        }

        adapter.setFilteredList(filteredList);
    }

    private void filterBy247() {
        List<HospitalModel> filteredList = new ArrayList<>();
        for (HospitalModel item : fullList) {
            // Assume any "Hospital" is open 24/7 for demo purposes
            if (item.getName().toLowerCase().contains("hospital")) {
                filteredList.add(item);
            }
        }
        adapter.setFilteredList(filteredList);
    }

    private void filterList(String text) {
        List<HospitalModel> filteredList = new ArrayList<>();
        for (HospitalModel item : fullList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) filteredList.add(item);
        }
        adapter.setFilteredList(filteredList);
    }

    private void updateChipUI(TextView selectedChip) {
        resetChip(chipAll); resetChip(chipGynac); resetChip(chipGeneral); resetChip(chip247);
        selectedChip.setBackgroundResource(R.drawable.btn_gradient_signup);
        selectedChip.setTextColor(android.graphics.Color.WHITE);
    }
    private void resetChip(TextView chip) {
        chip.setBackgroundResource(R.drawable.bg_chip_inactive);
        chip.setTextColor(android.graphics.Color.BLACK);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) getCurrentLocation();
    }
}
