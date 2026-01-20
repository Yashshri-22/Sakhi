package com.example.sakhi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private static final String SUPABASE_KEY =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InNic3Bxbm5tdWxsZXpscGJkemhzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Njg4MTc4NDYsImV4cCI6MjA4NDM5Mzg0Nn0.H9p0LoBRWEgjKBRSfKg1DdwnCN7qV2dQCo2gVEL7DiU";

    private static final int PICK_IMAGE = 201;

    private ShapeableImageView profileImage;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImage = findViewById(R.id.profileImage);
        profileImage.setOnClickListener(v -> openGallery());

        // Back arrow
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Edit profile button
        findViewById(R.id.btnEditProfile).setOnClickListener(v ->
                startActivity(new Intent(this, EditProfileActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfile();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            profileImage.setImageURI(selectedImageUri);
            uploadProfileImage();
        }
    }

    private void uploadProfileImage() {

        String userId = SessionManager.getUserId(this);
        String accessToken = SessionManager.getAccessToken(this);

        if (userId == null || accessToken == null || selectedImageUri == null) {
            Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(FileUtils.getPath(this, selectedImageUri));

        RequestBody requestFile =
                RequestBody.create(file, okhttp3.MediaType.parse("image/*"));

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        String fileName = userId + ".jpg";

        SupabaseStorageApi api =
                RetrofitClient.getClient().create(SupabaseStorageApi.class);

        api.uploadImage("Bearer " + accessToken, fileName, "true", body)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {

                            String imageUrl =
                                    "https://sbspqnnmullezlpbdzhs.supabase.co"
                                            + "/storage/v1/object/public/profile-images/"
                                            + fileName;

                            saveImageUrlToProfile(imageUrl);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(ProfileActivity.this,
                                t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveImageUrlToProfile(String imageUrl) {

        String userId = SessionManager.getUserId(this);
        String accessToken = SessionManager.getAccessToken(this);

        JsonObject body = new JsonObject();
        body.addProperty("id", userId);
        body.addProperty("image_url", imageUrl);

        SupabaseProfileApi api =
                RetrofitClient.getClient().create(SupabaseProfileApi.class);

        api.saveProfile(
                SUPABASE_KEY,
                "Bearer " + accessToken,
                "application/json",
                "resolution=merge-duplicates",
                body
        ).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> c, Response<Void> r) {}
            @Override public void onFailure(Call<Void> c, Throwable t) {}
        });
    }

    private void loadProfile() {

        String userId = SessionManager.getUserId(this);
        String accessToken = SessionManager.getAccessToken(this);

        if (userId == null || accessToken == null) {
            Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show();
            return;
        }

        SupabaseProfileApi api =
                RetrofitClient.getClient().create(SupabaseProfileApi.class);

        api.getProfile(
                SUPABASE_KEY,
                "Bearer " + accessToken,
                "eq." + userId,
                "*"
        ).enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().size() > 0) {

                    JsonObject profile = response.body().get(0).getAsJsonObject();

                    setField(R.id.fieldName, "Name", get(profile, "full_name"));
                    setField(R.id.fieldAge, "Age", get(profile, "age"));
                    setField(R.id.fieldHeight, "Height", get(profile, "height_cm") + " cm");
                    setField(R.id.fieldWeight, "Weight", get(profile, "weight_kg") + " kg");
                    setField(R.id.fieldCycle, "Cycle Length",
                            get(profile, "menstrual_cycle_length") + " days");
                    setField(R.id.fieldLastPeriod, "Last Period Date",
                            get(profile, "last_period_date"));
                    setField(R.id.fieldCondition, "Condition",
                            get(profile, "conditions"));

                    // ✅ IMAGE LOAD (CORRECT PLACE)
                    String imageUrl = get(profile, "image_url");
                    if (!imageUrl.equals("Not set")) {
                        Glide.with(ProfileActivity.this)
                                .load(imageUrl + "?t=" + System.currentTimeMillis()) // 👈 CACHE BUSTER
                                .placeholder(R.drawable.profile_image)
                                .skipMemoryCache(true)       // 👈 VERY IMPORTANT
                                .diskCacheStrategy(
                                        com.bumptech.glide.load.engine.DiskCacheStrategy.NONE
                                )
                                .into(profileImage);
                    }

                } else {
                    Toast.makeText(ProfileActivity.this,
                            "Profile not found",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(ProfileActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setField(int fieldId, String label, String value) {
        View field = findViewById(fieldId);
        TextView tvLabel = field.findViewById(R.id.tvLabel);
        TextView tvValue = field.findViewById(R.id.tvValue);

        tvLabel.setText(label);
        tvValue.setText(value == null || value.equals("null") ? "Not set" : value);
    }

    private String get(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull()
                ? obj.get(key).getAsString()
                : "Not set";
    }
}
