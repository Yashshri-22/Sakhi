package com.example.sakhi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.VH> {

    private final List<CalendarDay> days;
    private final SimpleDateFormat df =
            new SimpleDateFormat("d", Locale.US);

    public CalendarAdapter(List<CalendarDay> days) {
        this.days = days;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_day, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH h, int pos) {
        CalendarDay d = days.get(pos);

        if (d.date == null) {
            h.tv.setText("");
            h.tv.setBackground(null);
            return;
        }

        h.tv.setText(df.format(d.date));

        if (d.isPeriod)
            h.tv.setBackgroundResource(R.drawable.bg_day_period);
        else if (d.isOvulation)
            h.tv.setBackgroundResource(R.drawable.bg_day_ovulation);
        else if (d.isFertile)
            h.tv.setBackgroundResource(R.drawable.bg_day_fertile);
        else if (d.isToday)
            h.tv.setBackgroundResource(R.drawable.bg_day_today);
        else
            h.tv.setBackgroundResource(R.drawable.bg_day_normal);
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tv;
        VH(View v) {
            super(v);
            tv = v.findViewById(R.id.tvDay);
        }
    }
}
