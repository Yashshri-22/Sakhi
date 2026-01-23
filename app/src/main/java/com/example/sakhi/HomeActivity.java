package com.example.sakhi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.JsonObject;

public class HomeActivity extends AppCompatActivity {

    private ShapeableImageView imgProfile;
    private TextView tvGreeting, tvWelcome;

    private static final String SUPABASE_KEY =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InNic3Bxbm5tdWxsZXpscGJkemhzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Njg4MTc4NDYsImV4cCI6MjA4NDM5Mzg0Nn0.H9p0LoBRWEgjKBRSfKg1DdwnCN7qV2dQCo2gVEL7DiU";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        updateDailyChallengeCard();

        View challengeCard = findViewById(R.id.btnStartChallenge);

        if (challengeCard != null) {
            challengeCard.setOnClickListener(v -> {
                startActivity(
                        new Intent(
                                HomeActivity.this,
                                DailyChallengeActivity.class
                        )
                );
            });
        }

        // Bottom navigation
        BottomNavHelper.setupBottomNav(this, R.id.navHome);

        // Profile image click → custom menu
        imgProfile = findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(v -> showProfileMenu());

        View included = findViewById(R.id.trackSymptomsInclude);
        View btn = included.findViewById(R.id.btnTrack);

        tvGreeting = findViewById(R.id.tvGreeting);
        tvWelcome = findViewById(R.id.tvWelcome);

        // load user name from Supabase
        loadUserName();

        findViewById(R.id.btnPlayMythFact).setOnClickListener(v -> {

            if (MythFactManager.isCompleted(this)) {
                Toast.makeText(this, "You already played today 🌸", Toast.LENGTH_SHORT).show();
                return;
            }

            showMythFactGame();
        });



        btn.setOnClickListener(v -> {
            startActivity(new Intent(this, SymptomChatActivity.class));
        });

    }

    // THIS GOES HERE (not inside onCreate)
    @Override
    protected void onResume() {
        super.onResume();
        updateDailyChallengeCard();
    }

    private void showProfileMenu() {

        View menuView = getLayoutInflater()
                .inflate(R.layout.layout_profile_menu, null);

        PopupWindow popupWindow = new PopupWindow(
                menuView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
        );

        popupWindow.setElevation(20f);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        // Show menu near profile image (adjust if needed)
        popupWindow.showAsDropDown(imgProfile, -90, 20);

        // Profile
        menuView.findViewById(R.id.menuProfile).setOnClickListener(v -> {
            popupWindow.dismiss();
            startActivity(new Intent(this, ProfileActivity.class));
        });

        // Reminders
        menuView.findViewById(R.id.menuReminders).setOnClickListener(v -> {
            popupWindow.dismiss();
            startActivity(new Intent(this, RemainderActivity.class));
        });

        // Feedback
        menuView.findViewById(R.id.feedback).setOnClickListener(v -> {
            popupWindow.dismiss();
            startActivity(new Intent(this, FeedbackListActivity.class));
        });

        // Logout
        menuView.findViewById(R.id.menuLogout).setOnClickListener(v -> {
            popupWindow.dismiss();
            logoutUser();
        });
    }

    private void showMythFactGame() {

        MythFactQuestion q =
                MythFactManager.getTodayQuestion(this);

        View view = getLayoutInflater()
                .inflate(R.layout.bottomsheet_myth_fact, null);

        com.google.android.material.bottomsheet.BottomSheetDialog dialog =
                new com.google.android.material.bottomsheet.BottomSheetDialog(this);

        dialog.setContentView(view);

        TextView tvQ = view.findViewById(R.id.tvQuestion);
        TextView tvRes = view.findViewById(R.id.tvResult);
        Button myth = view.findViewById(R.id.btnMyth);
        Button fact = view.findViewById(R.id.btnFact);

        tvQ.setText(q.question);

        View.OnClickListener listener = v -> {

            boolean userAnswer = (v.getId() == R.id.btnFact);

            if (userAnswer == q.isFact) {
                tvRes.setText("Correct!\n" + q.explanation + "\n\n+10 points 🌸");
            } else {
                tvRes.setText("Oops!\n" + q.explanation);
            }

            tvRes.setVisibility(View.VISIBLE);
            myth.setEnabled(false);
            fact.setEnabled(false);

            MythFactManager.markCompleted(this);
        };

        myth.setOnClickListener(listener);
        fact.setOnClickListener(listener);

        dialog.show();
    }


    private void loadUserName() {

        String userId = SessionManager.getUserId(this);
        String token = SessionManager.getAccessToken(this);

        if (userId == null || token == null) return;

        SupabaseProfileApi api =
                RetrofitClient.getClient().create(SupabaseProfileApi.class);

        api.getProfile(
                SUPABASE_KEY,
                "Bearer " + token,
                "eq." + userId,
                "*"
        ).enqueue(new retrofit2.Callback<com.google.gson.JsonArray>() {

            @Override
            public void onResponse(
                    retrofit2.Call<com.google.gson.JsonArray> call,
                    retrofit2.Response<com.google.gson.JsonArray> response) {

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().size() > 0) {

                    JsonObject profile =
                            response.body().get(0).getAsJsonObject();

                    String fullName = profile.has("full_name")
                            ? profile.get("full_name").getAsString()
                            : profile.get("username").getAsString();

                    tvGreeting.setText("Hi, " + fullName);
                    tvWelcome.setText(
                            "Welcome, " + fullName + " 👋 Here are some fun health tasks for you!"
                    );
                }
            }

            @Override
            public void onFailure(
                    retrofit2.Call<com.google.gson.JsonArray> call,
                    Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void updateDailyChallengeCard() {

        View card = findViewById(R.id.btnStartChallenge).getRootView();

        Button btnStart =
                card.findViewById(R.id.btnStartChallenge);

        TextView tvStatus =
                card.findViewById(R.id.tvChallengeStatus);

        if (ChallengeManager.isTodayCompleted(this)) {

            btnStart.setText("Completed ✓");
            btnStart.setEnabled(false);
            btnStart.setAlpha(0.6f);

            tvStatus.setText("You earned +10 points today 🌸");

        } else {

            btnStart.setText("Start");
            btnStart.setEnabled(true);
            btnStart.setAlpha(1f);

            tvStatus.setText("Earn +10 points today 🌸");

            btnStart.setOnClickListener(v ->
                    startActivity(
                            new Intent(
                                    HomeActivity.this,
                                    DailyChallengeActivity.class
                            )
                    ));
        }
    }


    private void logoutUser() {
        // Clear Supabase session
        SessionManager.clearSession(this);

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
