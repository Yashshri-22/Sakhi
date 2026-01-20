package com.example.sakhi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ConditionAdapter extends RecyclerView.Adapter<ConditionAdapter.ViewHolder> {

    private List<ConditionModel> list;

    public ConditionAdapter(List<ConditionModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_condition_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConditionModel item = list.get(position);

        holder.icon.setImageResource(item.getIcon());
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView title, description;

        ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.imgIcon);
            title = itemView.findViewById(R.id.txtTitle);
            description = itemView.findViewById(R.id.txtDescription);
        }
    }
}
