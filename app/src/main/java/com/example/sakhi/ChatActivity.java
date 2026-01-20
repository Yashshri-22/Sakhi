package com.example.sakhi;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    // ▼▼▼ GET YOUR KEY FROM: aistudio.google.com ▼▼▼
    private static final String API_KEY = "AIzaSyBuv1xnML3CcvkV_XGEneDmnU4fU-3RfTI";

    RecyclerView rvChat;
    EditText etMessage;
    ImageButton btnSend;
    ImageView btnBack;

    ChatAdapter adapter;
    List<ChatMessage> chatList;
    GenerativeModelFutures model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);

        // 1. Setup RecyclerView
        chatList = new ArrayList<>();
        // Add a welcome message from AI
        chatList.add(new ChatMessage("Hi! I am Sakhi. Ask me anything about women's health, diet, or fitness.", false));

        adapter = new ChatAdapter(chatList);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);

        // 2. Setup AI Model (Gemini Flash is fast and free)
        GenerativeModel gm = new GenerativeModel("gemini-2.5-flash", API_KEY);
        model = GenerativeModelFutures.from(gm);

        // 3. Send Button Action
        btnSend.setOnClickListener(v -> sendMessage());
        btnBack.setOnClickListener(v -> finish());
    }

    private void sendMessage() {
        String userQuery = etMessage.getText().toString().trim();
        if (userQuery.isEmpty()) return;

        // Add User Message to List
        chatList.add(new ChatMessage(userQuery, true));
        adapter.notifyItemInserted(chatList.size() - 1);
        rvChat.scrollToPosition(chatList.size() - 1);
        etMessage.setText("");

        // 4. THE PROMPT ENGINEERING (Strict Rules)
        String strictPrompt = "You are a health assistant named Sakhi. " +
                "You ONLY answer questions related to medical health, women's wellness, fitness, diet, PCOD/PCOS, and mental well-being. " +
                "If the user asks about technology, coding, movies, politics, or general knowledge, " +
                "strictly reply: 'I can only assist with health-related queries.' " +
                "Do not answer off-topic questions. " +
                "Here is the user's question: " + userQuery;

        // 5. Call AI
        Content content = new Content.Builder().addText(strictPrompt).build();
        Executor executor = Executors.newSingleThreadExecutor();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String aiReply = result.getText();
                runOnUiThread(() -> {
                    chatList.add(new ChatMessage(aiReply, false));
                    adapter.notifyItemInserted(chatList.size() - 1);
                    rvChat.scrollToPosition(chatList.size() - 1);
                });
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> {
                    Toast.makeText(ChatActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }, executor);
    }
}
