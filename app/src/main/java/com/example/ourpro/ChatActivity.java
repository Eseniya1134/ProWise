package com.example.ourpro;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ourpro.bottomnav.dialogs.DialogsFragment;
import com.example.ourpro.bottomnav.profile.ProfileFragment;
import com.example.ourpro.databinding.ActivityChatBinding;
import com.example.ourpro.loginRegistration.SignInFragment;
import com.example.ourpro.message.Message;
import com.example.ourpro.message.MessagesAdapter;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.firestore.*;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.lang.ref.Reference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private final List<Message> messages = new ArrayList<>();
    private MessagesAdapter adapter;

    private String currentUserId;
    private String otherUserId;
    private String chatId;

    private FirebaseFirestore firestore;
    private DatabaseReference rtdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());




        firestore = FirebaseFirestore.getInstance();
        rtdb = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chatId = getIntent().getStringExtra("chatId");
        otherUserId = getIntent().getStringExtra("otherUserId");

        Log.e("ChatActivity", otherUserId + "  " + chatId + "  " + currentUserId);

        ensureChatExists(chatId, currentUserId, otherUserId);
        loadWindowInfo();
        setupRecyclerView();
        setupSendButton();
        loadMessages();


        binding.backButton.setOnClickListener(v -> {
            onBackPressed();
        });


    }

    // Проверяет, существует ли чат и создает его, если необходимо
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

    // Обновляет список чатов пользователя
    private void updateUserChats(String userId, String chatId) {
        DatabaseReference chatsRef = rtdb.child("Users").child(userId).child("chats");
        chatsRef.get().addOnSuccessListener(snapshot -> {
            String currentChats = snapshot.getValue(String.class);
            if (currentChats == null) currentChats = "";
            if (!currentChats.contains(chatId)) {
                chatsRef.setValue(currentChats + chatId + ","); // Добавление нового чата в список
            }
        });
    }

    // Настройка RecyclerView для отображения сообщений
    private void setupRecyclerView() {
        adapter = new MessagesAdapter(messages); // Инициализация адаптера для сообщений
        binding.messagesRv.setLayoutManager(new LinearLayoutManager(this));
        binding.messagesRv.setAdapter(adapter);
    }

    // Настройка кнопки отправки сообщения
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
                        .set(message) // Добавление нового сообщения в Firestore
                        .addOnSuccessListener(aVoid -> {
                            binding.messageEt.setText(""); // Очищаем поле ввода
                        });
            }
        });
    }

    // Загружает все сообщения из Firestore для текущего чата
    private void loadMessages() {
        firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("date", Query.Direction.ASCENDING) // Сортировка сообщений по времени
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;

                    messages.clear(); // Очищаем список сообщений перед загрузкой новых
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        try {
                            String id = doc.getString("id");
                            String senderId = doc.getString("senderId");
                            String text = doc.getString("text");

                            Object dateObj = doc.get("date");
                            Timestamp date;

                            if (dateObj instanceof Timestamp) {
                                date = (Timestamp) dateObj; // Если дата - это объект Timestamp
                            } else if (dateObj instanceof String) {
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm MM-dd", Locale.getDefault());
                                    Date parsedDate = sdf.parse((String) dateObj); // Парсим строку в объект Date
                                    date = parsedDate != null ? new Timestamp(parsedDate) : Timestamp.now();
                                } catch (ParseException e) {
                                    date = Timestamp.now(); // fallback
                                }
                            } else {
                                date = Timestamp.now(); // fallback, если дата не определена
                            }

                            // Добавляем сообщение в список
                            if (id != null && senderId != null && text != null && date != null) {
                                messages.add(new Message(id, senderId, text, date));
                            } else {
                                // Логирование пропущенных сообщений с null полями
                                Log.w("ChatActivity", "Пропущено сообщение с null полями: " + doc.getId());
                            }

                        } catch (Exception e) {
                            e.printStackTrace(); // Сообщение не добавляется в случае ошибки
                        }
                    }

                    adapter.notifyDataSetChanged(); // Уведомляем адаптер о новых данных
                    binding.messagesRv.scrollToPosition(messages.size() - 1); // Прокручиваем до последнего сообщения
                });
    }


    //Загрузка информации
    private void loadWindowInfo() {
        DatabaseReference userRef = rtdb.child("Users").child(otherUserId);

        userRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String name = snapshot.child("name").getValue(String.class);
                String surname = snapshot.child("surname").getValue(String.class);
                String fullName = (name != null ? name : "") + " " + (surname != null ? surname : "");

                binding.usernameTv.setText(fullName.trim());
            } else {
                binding.usernameTv.setText("Неизвестный пользователь");
            }
        }).addOnFailureListener(e -> {
            Log.e(ContentValues.TAG, "Ошибка загрузки имени пользователя: " + e.getMessage());
            binding.usernameTv.setText("Ошибка загрузки");
        });

        userRef.child("profileImageURL").get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        String imageUrl = dataSnapshot.getValue(String.class);
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Picasso.get().load(imageUrl).into(binding.profileIv);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Ошибка загрузки изображения из Firebase: " + e.getMessage());
                });
    }


}
