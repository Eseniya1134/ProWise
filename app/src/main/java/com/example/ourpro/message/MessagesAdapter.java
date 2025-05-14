package com.example.ourpro.message;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourpro.databinding.MessageFromCurrUserRvItemBinding;
import com.example.ourpro.databinding.MessageRvItemBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Message> messages; // Список сообщений
    private static final int VIEW_TYPE_MY_MESSAGE = 1; // Тип для сообщений текущего пользователя
    private static final int VIEW_TYPE_OTHER_MESSAGE = 2; // Тип для сообщений других пользователей

    // Конструктор, инициализирует список сообщений
    public MessagesAdapter(List<Message> messages) {
        this.messages = messages;
    }

    // Определяет тип элемента (сообщение от текущего пользователя или другого)
    @Override
    public int getItemViewType(int position) {
        String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        // Если ID отправителя совпадает с ID текущего пользователя, то это его сообщение
        return messages.get(position).getSenderId().equals(currentUserId)
                ? VIEW_TYPE_MY_MESSAGE : VIEW_TYPE_OTHER_MESSAGE;
    }

    // Создаёт ViewHolder в зависимости от типа сообщения
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MY_MESSAGE) {
            // Если это сообщение текущего пользователя, используем привязку для макета "мой сообщение"
            MessageFromCurrUserRvItemBinding binding = MessageFromCurrUserRvItemBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new MyMessageViewHolder(binding); // Возвращаем ViewHolder для своего сообщения
        } else {
            // Если это сообщение другого пользователя, используем привязку для макета "чужое сообщение"
            MessageRvItemBinding binding = MessageRvItemBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new OtherMessageViewHolder(binding); // Возвращаем ViewHolder для чужого сообщения
        }
    }

    // Привязывает данные к соответствующему ViewHolder
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position); // Получаем сообщение по позиции
        if (holder instanceof MyMessageViewHolder) {
            // Если это ViewHolder для своего сообщения, привязываем данные
            ((MyMessageViewHolder) holder).bind(message);
        } else {
            // Если это ViewHolder для чужого сообщения, привязываем данные
            ((OtherMessageViewHolder) holder).bind(message);
        }
    }

    // Возвращает общее количество сообщений
    @Override
    public int getItemCount() {
        return messages.size(); // Возвращаем размер списка сообщений
    }

    // Форматирует Timestamp в строку для отображения времени сообщения
    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) return ""; // Если временная метка пуста, возвращаем пустую строку
        Date date = timestamp.toDate(); // Преобразуем временную метку в объект Date
        // Форматируем дату в строку с нужным форматом
        return new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault()).format(date);
    }

    // ViewHolder для сообщений текущего пользователя
    class MyMessageViewHolder extends RecyclerView.ViewHolder {
        private final MessageFromCurrUserRvItemBinding binding;

        public MyMessageViewHolder(@NonNull MessageFromCurrUserRvItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        // Привязка данных (текста сообщения и времени)
        public void bind(Message message) {
            binding.messageTv.setText(message.getText()); // Устанавливаем текст сообщения
            binding.messageDateTv.setText(formatTimestamp(message.getDate())); // Устанавливаем отформатированное время
        }
    }

    // ViewHolder для сообщений других пользователей
    class OtherMessageViewHolder extends RecyclerView.ViewHolder {
        private final MessageRvItemBinding binding;

        public OtherMessageViewHolder(@NonNull MessageRvItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        // Привязка данных (текста сообщения и времени)
        public void bind(Message message) {
            binding.messageTv.setText(message.getText()); // Устанавливаем текст сообщения
            binding.messageDateTv.setText(formatTimestamp(message.getDate())); // Устанавливаем отформатированное время
        }
    }

    //Возвращает количество непрочитанных сообщений
    public int getUnreadCount(Timestamp lastSeenTime) {
        if (lastSeenTime == null) return 0;

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "";

        int unreadCount = 0;
        for (Message message : messages) {
            if (!message.getSenderId().equals(currentUserId)
                    && message.getDate() != null
                    && message.getDate().compareTo(lastSeenTime) > 0) {
                unreadCount++;
            }
        }
        return unreadCount;
    }

}
