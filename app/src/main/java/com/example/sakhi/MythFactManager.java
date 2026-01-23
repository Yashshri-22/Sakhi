package com.example.sakhi;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MythFactManager {

    private static final String PREF = "myth_fact_prefs";
    private static final String KEY_INDEX = "today_index";
    private static final String KEY_DAY = "saved_day";
    private static final String KEY_DONE = "done";

    public static List<MythFactQuestion> getQuestions() {

        List<MythFactQuestion> list = new ArrayList<>();

        list.add(new MythFactQuestion(
                "Periods should always be painful.",
                false,
                "Fact: Severe pain is not normal and may need medical attention."
        ));

        list.add(new MythFactQuestion(
                "You can exercise during periods.",
                true,
                "Fact: Light exercise can reduce cramps and improve mood."
        ));

        list.add(new MythFactQuestion(
                "Irregular cycles are always unhealthy.",
                false,
                "Fact: Stress, diet, and hormones can affect cycles."
        ));

        list.add(new MythFactQuestion(
                "Periods cleanse the body of toxins.",
                false,
                "Fact: The liver and kidneys handle detoxification."
        ));

        list.add(new MythFactQuestion(
                "Ovulation pain can happen.",
                true,
                "Fact: Some women feel mild ovulation pain."
        ));

        return list;
    }

    public static MythFactQuestion getTodayQuestion(Context context) {

        SharedPreferences sp =
                context.getSharedPreferences(PREF, Context.MODE_PRIVATE);

        int today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        int savedDay = sp.getInt(KEY_DAY, -1);

        List<MythFactQuestion> list = getQuestions();

        if (today != savedDay) {
            int index = today % list.size();
            sp.edit()
                    .putInt(KEY_INDEX, index)
                    .putInt(KEY_DAY, today)
                    .putBoolean(KEY_DONE, false)
                    .apply();
        }

        return list.get(sp.getInt(KEY_INDEX, 0));
    }

    public static boolean isCompleted(Context context) {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .getBoolean(KEY_DONE, false);
    }

    public static void markCompleted(Context context) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_DONE, true)
                .apply();
    }
}
