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

    private final List<Message> messages;
    private static final int VIEW_TYPE_MY_MESSAGE = 1;
    private static final int VIEW_TYPE_OTHER_MESSAGE = 2;

    public MessagesAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        return messages.get(position).getSenderId().equals(currentUserId)
                ? VIEW_TYPE_MY_MESSAGE : VIEW_TYPE_OTHER_MESSAGE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MY_MESSAGE) {
            MessageFromCurrUserRvItemBinding binding = MessageFromCurrUserRvItemBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new MyMessageViewHolder(binding);
        } else {
            MessageRvItemBinding binding = MessageRvItemBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new OtherMessageViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (holder instanceof MyMessageViewHolder) {
            ((MyMessageViewHolder) holder).bind(message);
        } else {
            ((OtherMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) return "";
        Date date = timestamp.toDate();
        return new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault()).format(date);
    }

    class MyMessageViewHolder extends RecyclerView.ViewHolder {
        private final MessageFromCurrUserRvItemBinding binding;

        public MyMessageViewHolder(@NonNull MessageFromCurrUserRvItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Message message) {
            binding.messageTv.setText(message.getText());
            binding.messageDateTv.setText(formatTimestamp(message.getDate()));
        }
    }

    class OtherMessageViewHolder extends RecyclerView.ViewHolder {
        private final MessageRvItemBinding binding;

        public OtherMessageViewHolder(@NonNull MessageRvItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Message message) {
            binding.messageTv.setText(message.getText());
            binding.messageDateTv.setText(formatTimestamp(message.getDate()));
        }
    }
}
