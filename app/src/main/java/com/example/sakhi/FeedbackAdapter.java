package com.example.sakhi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sakhi.R;
import org.json.JSONObject;
import java.util.ArrayList;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder> {

    private ArrayList<JSONObject> list;

    public FeedbackAdapter(ArrayList<JSONObject> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject item = list.get(position);
            holder.textFeedback.setText(item.optString("text", ""));

            // Handle Rating
            double rating = item.optDouble("rating", 5.0);
            holder.ratingBar.setRating((float) rating);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textFeedback;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textFeedback = itemView.findViewById(R.id.tvFeedbackText);
            ratingBar = itemView.findViewById(R.id.ratingBarSmall);
        }
    }
}
