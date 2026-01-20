package com.example.sakhi;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddRemainderActivity extends AppCompatActivity {

    private EditText etTitle, etNotes;
    private Spinner repeatSpinner, ampmSpinner;
    private TextView tvHour, tvMinute;
    private SwitchCompat switchReminder;

    // Store the raw 24h time for the system alarm
    private int systemHour = -1;
    private int systemMinute = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remainder);

        // 1. Initialize Views
        etTitle = findViewById(R.id.etTitle);
        etNotes = findViewById(R.id.etNotes);
        repeatSpinner = findViewById(R.id.repeatSpinner);
        ampmSpinner = findViewById(R.id.ampmSpinner);
        tvHour = findViewById(R.id.tvHour);
        tvMinute = findViewById(R.id.tvMinute);
        switchReminder = findViewById(R.id.switchReminder);
        Button btnSet = findViewById(R.id.btnSet);
        Button btnCancel = findViewById(R.id.btnCancel);
        ImageView btnBack = findViewById(R.id.btnBack);

        // Check Notification Permission (Required for Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        setupSpinners();

        // 2. Time Picker Listener
        View.OnClickListener timeClickListener = v -> showTimePicker();
        tvHour.setOnClickListener(timeClickListener);
        tvMinute.setOnClickListener(timeClickListener);

        // 3. Button Actions
        btnSet.setOnClickListener(v -> saveReminder());

        btnCancel.setOnClickListener(v -> finish());
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupSpinners() {
        String[] repeatOptions = {"Daily", "Weekly", "Monthly", "Once"};
        ArrayAdapter<String> repeatAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, repeatOptions);
        repeatSpinner.setAdapter(repeatAdapter);

        String[] ampmOptions = {"AM", "PM"};
        ArrayAdapter<String> ampmAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ampmOptions);
        ampmSpinner.setAdapter(ampmAdapter);
    }

    private void showTimePicker() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        TimePickerDialog mTimePicker = new TimePickerDialog(this, (timePicker, selectedHourOfDay, selectedMinuteOfHour) -> {
            // Save the raw system time for the AlarmManager
            systemHour = selectedHourOfDay;
            systemMinute = selectedMinuteOfHour;

            // Display logic (12h format)
            int hour12 = selectedHourOfDay;
            String ampm = "AM";
            if (hour12 >= 12) {
                ampm = "PM";
                if (hour12 > 12) hour12 -= 12;
            }
            if (hour12 == 0) hour12 = 12;

            tvHour.setText(String.format(Locale.getDefault(), "%02d", hour12));
            tvMinute.setText(String.format(Locale.getDefault(), "%02d", selectedMinuteOfHour));

            if (ampm.equals("AM")) ampmSpinner.setSelection(0);
            else ampmSpinner.setSelection(1);

        }, hour, minute, false);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void saveReminder() {
        String title = etTitle.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();
        String repeat = repeatSpinner.getSelectedItem().toString();
        boolean isActive = switchReminder.isChecked();

        // 1. Validation
        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }
        if (systemHour == -1) {
            Toast.makeText(this, "Please select a time first", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- PART A: SAVE TO LIST (So it shows up on the previous page) ---

        // Format Time nicely for display (e.g., "10:30 AM")
        String ampm = "AM";
        int hour12 = systemHour;
        if (hour12 >= 12) { ampm = "PM"; if (hour12 > 12) hour12 -= 12; }
        if (hour12 == 0) hour12 = 12;
        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d %s", hour12, systemMinute, ampm);

        // Load existing list
        SharedPreferences prefs = getSharedPreferences("SakhiData", MODE_PRIVATE);
        String json = prefs.getString("reminders", null);
        Gson gson = new Gson();
        List<RemainderModel> list;

        if (json == null) {
            list = new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<RemainderModel>>() {}.getType();
            list = gson.fromJson(json, type);
        }

        // Add new reminder
        list.add(new RemainderModel(title, formattedTime, repeat, isActive));

        // Save back to storage
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("reminders", gson.toJson(list));
        editor.apply();


        // --- PART B: SET THE REAL ALARM (Notification) ---

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Check Permission for Android 12+ (API 31+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                Toast.makeText(this, "Please allow 'Alarms & Reminders' permission", Toast.LENGTH_LONG).show();
                return;
            }
        }

        // Prepare the Intent
        Intent intent = new Intent(this, RemainderReceiver.class);
        intent.putExtra("title", "Sakhi Reminder: " + title);
        intent.putExtra("message", notes.isEmpty() ? "It's time for your habit!" : notes);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Set the Calendar time
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, systemHour);
        calendar.set(Calendar.MINUTE, systemMinute);
        calendar.set(Calendar.SECOND, 0);

        // If time is in the past, add 1 day
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
            Toast.makeText(this, "Reminder Saved & Alarm Set!", Toast.LENGTH_SHORT).show();
            finish(); // Close screen and return to list
        } catch (SecurityException e) {
            Toast.makeText(this, "Permission Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

