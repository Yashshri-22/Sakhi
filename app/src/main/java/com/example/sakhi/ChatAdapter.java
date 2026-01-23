package com.example.sakhi;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> chatList;

    public ChatAdapter(List<ChatMessage> chatList) {
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // We use a simple layout for rows
        LinearLayout layout = new LinearLayout(parent.getContext());
        layout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(10, 10, 10, 10);

        return new ChatViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage chat = chatList.get(position);

        // Styling logic
        if (chat.isUser) {
            // User Message (Right Side, Light Pink)
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FADADD"));
            holder.layout.setGravity(Gravity.END);
            holder.tvMessage.setTextColor(Color.parseColor("#4A2C2A"));
        } else {
            // AI Message (Left Side, Dark Pink)
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FB6F97"));
            holder.layout.setGravity(Gravity.START);
            holder.tvMessage.setTextColor(Color.WHITE);
        }

        holder.tvMessage.setText(chat.message);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        CardView cardView;
        TextView tvMessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView;

            // Create CardView programmatically to avoid extra XML files
            cardView = new CardView(itemView.getContext());
            cardView.setRadius(30f);
            cardView.setCardElevation(4f);
            cardView.setContentPadding(30, 20, 30, 20);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 5, 10, 5);
            cardView.setLayoutParams(params);

            tvMessage = new TextView(itemView.getContext());
            tvMessage.setTextSize(16f);

            cardView.addView(tvMessage);
            layout.addView(cardView);
        }
    }
}
