package com.example.sakhi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RemainderAdapter extends RecyclerView.Adapter<RemainderAdapter.ViewHolder> {

        private List<RemainderModel> remainderList;

        public RemainderAdapter(List<RemainderModel> remainderList) {
            this.remainderList = remainderList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_remainder, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            RemainderModel item = remainderList.get(position);
            holder.tvTitle.setText(item.title);
            holder.tvTime.setText(item.repeat + " | " + item.time);
            holder.switchToggle.setChecked(item.isActive);
        }

        @Override
        public int getItemCount() {
            return remainderList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvTime;
            SwitchCompat switchToggle;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.itemTitle);
                tvTime = itemView.findViewById(R.id.itemTimeTag);
                switchToggle = itemView.findViewById(R.id.reminderToggle);
            }
        }
}