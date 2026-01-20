package com.example.sakhi;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupField(R.id.fieldName, "Name", "Not set");
        setupField(R.id.fieldAge, "Age", "Not set");
        setupField(R.id.fieldHeight, "Height", "Not set");
        setupField(R.id.fieldWeight, "Weight", "Not set");
        setupField(R.id.fieldCycle, "Cycle Length", "Not set");
        setupField(R.id.fieldCondition, "Condition", "Not set");
    }

    private void setupField(int fieldId, String label, String value) {
        View field = findViewById(fieldId);

        TextView tvLabel = field.findViewById(R.id.tvLabel);
        TextView tvValue = field.findViewById(R.id.tvValue);

        tvLabel.setText(label);
        tvValue.setText(value);
    }
}
