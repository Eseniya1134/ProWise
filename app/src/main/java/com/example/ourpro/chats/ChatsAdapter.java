package com.example.ourpro.chats;

import static androidx.fragment.app.FragmentManager.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ourpro.ChatActivity;
import com.example.ourpro.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;



public class ChatsAdapter extends RecyclerView.Adapter<ChatViewHolder>{

    private ArrayList<Chat> chats;

    public ChatsAdapter(ArrayList<Chat> chats){
        this.chats = chats;
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

        holder.chat_name_tv.setText(chats.get(position).getChat_name());

        String userId;
        if (!chats.get(position).getUserId1().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            userId = chats.get(position).getUserId1();
        } else {
            userId = chats.get(position).getUserId2();
        }

        /*FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference()
                .child("Users")
                .child(userId)
                .child("username")
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        String imageUrl = dataSnapshot.getValue(String.class);
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            // Загрузка изображения с помощью Picasso
                            Picasso.get()
                                    .load(imageUrl)
                                    .placeholder(R.drawable.profile_icon) // опционально: пока загружается
                                   // .error(R.drawable.profile_icon)         // опционально: если ошибка
                                    .into(holder.chat_iv);
                        }
                    }

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(holder.itemView.getContext(), "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show();
                });*/


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), ChatActivity.class);
            intent.putExtra("chatId", chats.get(position).getChat_id());

            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String otherUserId = currentUserId.equals(chat.getUserId1()) ? chat.getUserId2() : chat.getUserId1();
            intent.putExtra("otherUserId", otherUserId);

            holder.itemView.getContext().startActivity(intent);


        });
    }


    @Override
    public int getItemCount() {
        return chats.size();
    }
}