package com.example.sakhi;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class DailyChallengeActivity extends AppCompatActivity {

    TextView tvTitle, tvDesc;
    Button btnAction1, btnAction2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_challenge);

        tvTitle = findViewById(R.id.tvTitle);
        tvDesc = findViewById(R.id.tvDesc);
        btnAction1 = findViewById(R.id.btnAction1);
        btnAction2 = findViewById(R.id.btnAction2);

        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int type = day % 3;

        if (type == 0) loadTapChallenge();
        else if (type == 1) loadTimerChallenge();
        else loadQuizChallenge();
    }

    private void loadTapChallenge() {
        tvTitle.setText("💧 Hydration Challenge");
        tvDesc.setText("Tap once after each glass of water");

        btnAction1.setText("Glass 1");
        btnAction2.setText("Glass 2");

        btnAction1.setOnClickListener(v -> {
            btnAction1.setEnabled(false);
            checkComplete();
        });

        btnAction2.setOnClickListener(v -> {
            btnAction2.setEnabled(false);
            checkComplete();
        });
    }

    private void loadTimerChallenge() {

        tvTitle.setText("🧘‍♀️ Stretch Break");
        tvDesc.setText("Stretch for 60 seconds");

        btnAction1.setText("Start Timer");
        btnAction2.setVisibility(View.GONE);

        btnAction1.setOnClickListener(v -> {

            btnAction1.setEnabled(false);

            new CountDownTimer(60000, 1000) {

                public void onTick(long millisUntilFinished) {
                    btnAction1.setText(
                            "⏳ " + (millisUntilFinished / 1000) + " sec left"
                    );
                }

                public void onFinish() {
                    btnAction1.setText("Completed 🎉");
                    btnAction1.setEnabled(true);
                    finishChallenge();

                    Toast.makeText(
                            DailyChallengeActivity.this,
                            "Great job! You completed today's challenge 🌸",
                            Toast.LENGTH_SHORT
                    ).show();

                    // optional: auto close after 1 sec
                    btnAction1.postDelayed(() -> finish(), 1000);
                }

            }.start();
        });
    }


    private void loadQuizChallenge() {
        tvTitle.setText("🩸 Health Check");
        tvDesc.setText("Iron helps reduce fatigue. True or False?");

        btnAction1.setText("True");
        btnAction2.setText("False");

        btnAction1.setOnClickListener(v -> complete(true));
        btnAction2.setOnClickListener(v -> complete(false));
    }

    private void complete(boolean correct) {
        Toast.makeText(
                this,
                correct ? "Correct! 🎉" : "Oops! Still learning 😊",
                Toast.LENGTH_SHORT
        ).show();
        finishChallenge();
    }

    private void checkComplete() {
        if (!btnAction1.isEnabled() && !btnAction2.isEnabled()) {
            Toast.makeText(this, "Challenge completed 🌟", Toast.LENGTH_SHORT).show();
            finishChallenge();
        }
    }

    private void finishChallenge() {
        ChallengeManager.completeToday(this);

        Toast.makeText(
                this,
                "+10 points earned 🌟",
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }

}
