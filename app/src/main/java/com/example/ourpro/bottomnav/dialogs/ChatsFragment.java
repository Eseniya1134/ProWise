package com.example.ourpro.bottomnav.dialogs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ourpro.chats.Chat;
import com.example.ourpro.chats.ChatsAdapter;
import com.example.ourpro.databinding.FragmentChatsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatsFragment extends Fragment {

    private FragmentChatsBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        loadChats();
        return binding.getRoot();
    }

    private void loadChats() {
        ArrayList<Chat> chats = new ArrayList<>();
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        FirebaseDatabase db = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference rootRef = db.getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Получаем строку чатов текущего пользователя
                String chatsStr = snapshot.child("Users").child(uid).child("chats").getValue(String.class);
                if (chatsStr == null || chatsStr.isEmpty()) return;

                String[] chatIds = chatsStr.split(",");
                for (String chatId : chatIds) {
                    DataSnapshot chatSnapshot = snapshot.child("Chats").child(chatId);
                    if (!chatSnapshot.exists()) continue;

                    String user1 = chatSnapshot.child("user1").getValue(String.class);
                    String user2 = chatSnapshot.child("user2").getValue(String.class);
                    if (user1 == null || user2 == null) continue;

                    String otherUserId = uid.equals(user1) ? user2 : user1;

                    DataSnapshot userSnapshot = snapshot.child("Users").child(otherUserId);
                    String chatName = userSnapshot.child("username").getValue(String.class);
                    if (chatName == null) chatName = "unknown";

                    chats.add(new Chat(user2, user1, chatName, chatId));
                }

                // Обновляем UI после обработки всех чатов
                binding.chatsRv.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.chatsRv.setAdapter(new ChatsAdapter(chats));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Ошибка загрузки чатов", Toast.LENGTH_SHORT).show();
                Log.e("ChatsFragment", "Database error: " + error.getMessage());
            }
        });
    }
}