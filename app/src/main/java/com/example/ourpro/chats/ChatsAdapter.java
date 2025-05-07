package com.example.ourpro.chats;

import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourpro.ChatActivity;
import com.example.ourpro.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatsAdapter extends RecyclerView.Adapter<ChatViewHolder> {

    private final ArrayList<Chat> chats;
    private final DatabaseReference rtdb;

    public ChatsAdapter(ArrayList<Chat> chats) {
        this.chats = chats;
        this.rtdb = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app").getReference();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chats.get(position);

        bindChatName(holder, chat);
        bindProfileImage(holder, chat);
        setupClickListener(holder, chat);
    }

    private void bindChatName(@NonNull ChatViewHolder holder, @NonNull Chat chat) {
        holder.chat_name_tv.setText(chat.getChat_name());
    }

    private void bindProfileImage(@NonNull ChatViewHolder holder, @NonNull Chat chat) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String otherUserId = currentUserId.equals(chat.getUserId1()) ? chat.getUserId2() : chat.getUserId1();

        rtdb.child("Users").child(otherUserId).child("profileImageURL").get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        String imageUrl = snapshot.getValue(String.class);
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Picasso.get()
                                    .load(imageUrl)
                                    .placeholder(R.drawable.profile_icon)
                                    .error(R.drawable.profile_icon)
                                    .into(holder.chat_iv);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Log.e(ContentValues.TAG, "Ошибка загрузки изображения: " + e.getMessage())
                );
    }

    private void setupClickListener(@NonNull ChatViewHolder holder, @NonNull Chat chat) {
        holder.itemView.setOnClickListener(v -> {
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String otherUserId = currentUserId.equals(chat.getUserId1()) ? chat.getUserId2() : chat.getUserId1();

            Intent intent = new Intent(holder.itemView.getContext(), ChatActivity.class);
            intent.putExtra("chatId", chat.getChat_id());
            intent.putExtra("otherUserId", otherUserId);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }
}
