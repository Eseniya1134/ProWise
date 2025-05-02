package com.example.ourpro.message;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ourpro.databinding.FragmentMessageBinding;
import com.example.ourpro.message.Message;
import com.example.ourpro.message.MessagesAdapter;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.firestore.*;
import com.google.firebase.firestore.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MessageFragment extends Fragment {

    private FragmentMessageBinding binding;
    private final List<Message> messages = new ArrayList<>();
    private MessagesAdapter adapter;

    private String currentUserId;
    private String otherUserId;
    private String chatId;

    private FirebaseFirestore firestore;
    private DatabaseReference rtdb;

    public static MessageFragment newInstance(String otherUserId, String chatId) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString("otherUserId", otherUserId);
        args.putString("chatId", chatId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMessageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (getArguments() != null) {
            otherUserId = getArguments().getString("otherUserId");
            chatId = getArguments().getString("chatId");
        }

        firestore = FirebaseFirestore.getInstance();
        rtdb = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        ensureChatExists(chatId, currentUserId, otherUserId);
        setupRecyclerView();
        setupSendButton();
        loadMessages();
    }

    private void ensureChatExists(String chatId, String user1, String user2) {
        rtdb.child("Chats").child(chatId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    rtdb.child("Chats").child(chatId).child("user1").setValue(user1);
                    rtdb.child("Chats").child(chatId).child("user2").setValue(user2);

                    updateUserChats(user1, chatId);
                    updateUserChats(user2, chatId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateUserChats(String userId, String chatId) {
        DatabaseReference chatsRef = rtdb.child("Users").child(userId).child("chats");
        chatsRef.get().addOnSuccessListener(snapshot -> {
            String currentChats = snapshot.getValue(String.class);
            if (currentChats == null) currentChats = "";
            if (!currentChats.contains(chatId)) {
                chatsRef.setValue(currentChats + chatId + ",");
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new MessagesAdapter(messages);
        binding.messagesRv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.messagesRv.setAdapter(adapter);
    }

    private void setupSendButton() {
        binding.sendMessageBtn.setOnClickListener(v -> {
            String text = binding.messageEt.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                Message message = new Message(
                        UUID.randomUUID().toString(),
                        currentUserId,
                        text,
                        Timestamp.now()
                );

                firestore.collection("chats")
                        .document(chatId)
                        .collection("messages")
                        .document(message.getId())
                        .set(message)
                        .addOnSuccessListener(aVoid -> {
                            binding.messageEt.setText("");
                        });
            }
        });
    }

    private void loadMessages() {
        firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("date", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;

                    messages.clear();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        try {
                            String id = doc.getString("id");
                            String senderId = doc.getString("senderId");
                            String text = doc.getString("text");

                            Object dateObj = doc.get("date");
                            Timestamp date;

                            if (dateObj instanceof Timestamp) {
                                date = (Timestamp) dateObj;
                            } else if (dateObj instanceof String) {
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                                    Date parsedDate = sdf.parse((String) dateObj);
                                    date = parsedDate != null ? new Timestamp(parsedDate) : Timestamp.now();
                                } catch (ParseException e) {
                                    date = Timestamp.now();
                                }
                            } else {
                                date = Timestamp.now();
                            }

                            if (id != null && senderId != null && text != null && date != null) {
                                messages.add(new Message(id, senderId, text, date));
                            } else {
                                Log.w("ChatFragment", "Пропущено сообщение с null полями: " + doc.getId());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    adapter.notifyDataSetChanged();
                    binding.messagesRv.scrollToPosition(messages.size() - 1);
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
