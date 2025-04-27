package com.example.ourpro.bottomnav.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ourpro.chats.Chat;
import com.example.ourpro.chats.ChatsAdapter;
import com.example.ourpro.databinding.FragmentChatsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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
        FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String chatsStr = snapshot.child("Users").child(uid).child("chats").getValue(String.class);

                        if (chatsStr == null || chatsStr.isEmpty()) {
                            Toast.makeText(getContext(), "У вас пока нет чатов", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String[] chatsIds = chatsStr.split(",");
                        if (chatsIds.length == 0) {
                            Toast.makeText(getContext(), "У вас пока нет чатов", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (String chatId : chatsIds) {
                            chatId = chatId.trim();
                            if (chatId.isEmpty()) continue;

                            DataSnapshot chatSnapshot = snapshot.child("Chats").child(chatId);
                            if (!chatSnapshot.exists()) continue;

                            DataSnapshot chatUsersSnapshot = chatSnapshot.child("Users");
                            ArrayList<String> userIds = new ArrayList<>();
                            for (DataSnapshot userSnapshot : chatUsersSnapshot.getChildren()) {
                                userIds.add(userSnapshot.getKey());
                            }

                            if (userIds.size() != 2) continue;

                            String userId1 = userIds.get(0);
                            String userId2 = userIds.get(1);

                            String chatUserId = uid.equals(userId1) ? userId2 : userId1;

                            DataSnapshot chatUserSnapshot = snapshot.child("Users").child(chatUserId);
                            if (!chatUserSnapshot.exists()) continue;

                            String chatName = chatUserSnapshot.child("name").getValue(String.class);
                            if (chatName == null) chatName = "Неизвестный пользователь";

                            Chat chat = new Chat(chatId, chatName, userId1, userId2);
                            chats.add(chat);
                        }

                        binding.chatsRv.setLayoutManager(new LinearLayoutManager(getContext()));
                        binding.chatsRv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                        binding.chatsRv.setAdapter(new ChatsAdapter(chats));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Не удалось загрузить чаты", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
