package com.example.ourpro;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ourpro.databinding.ActivityChatBinding;
import com.example.ourpro.message.Message;
import com.example.ourpro.message.MessagesAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private final List<Message> messages = new ArrayList<>();
    private MessagesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupRecyclerView();
        setupSendButton();
    }

    private void setupRecyclerView() {
        adapter = new MessagesAdapter(messages);
        binding.messagesRv.setLayoutManager(new LinearLayoutManager(this));
        binding.messagesRv.setAdapter(adapter);
    }

    private void setupSendButton() {
        binding.sendMessageBtn.setOnClickListener(v -> {
            String text = binding.messageEt.getText().toString().trim();
            if (!text.isEmpty()) {
                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String date = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(new Date());

                Message message = new Message(
                        UUID.randomUUID().toString(), // генерируем id
                        currentUserId,
                        text,
                        date
                );
                messages.add(message);
                adapter.notifyItemInserted(messages.size() - 1);
                binding.messagesRv.scrollToPosition(messages.size() - 1);
                binding.messageEt.setText("");
            }
        });
    }
}
