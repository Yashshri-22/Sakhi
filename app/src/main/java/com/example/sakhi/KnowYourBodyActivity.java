package com.example.sakhi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class KnowYourBodyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_know_your_body);

        RecyclerView recyclerView = findViewById(R.id.rvConditions);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<ConditionModel> conditionList = new ArrayList<>();

        conditionList.add(new ConditionModel(
                R.drawable.ic_anemia,
                getString(R.string.anemia),
                getString(R.string.anemia_desc)
        ));

        conditionList.add(new ConditionModel(
                R.drawable.ic_pcos,
                getString(R.string.pcos),
                getString(R.string.pcos_desc)
        ));

        conditionList.add(new ConditionModel(
                R.drawable.ic_thyroid,
                getString(R.string.thyroid),
                getString(R.string.thyroid_desc)
        ));

        ConditionAdapter adapter = new ConditionAdapter(conditionList);
        recyclerView.setAdapter(adapter);
    }
}