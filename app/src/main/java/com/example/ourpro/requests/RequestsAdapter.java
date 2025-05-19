package com.example.ourpro.requests;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ourpro.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestViewHolder> {
    private List<ClientRequest> requests;
    private final OnRequestClickListener listener;

    public interface OnRequestClickListener {
        void onRequestClick(ClientRequest request);
    }

    public RequestsAdapter(List<ClientRequest> requests, OnRequestClickListener listener) {
        this.requests = requests != null ? requests : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_requests, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        ClientRequest request = requests.get(position);

        // Установка данных
        holder.requestText.setText(String.format("Заявка №%d", position + 1));
        holder.descriptionText.setText(request.getShortDescription());
        holder.deadlineText.setText(formatDeadline(request.getDeadline()));
        holder.statusText.setText(getStatusText(request.getStatus()));
        holder.statusText.setTextColor(getStatusColor(holder.itemView.getContext(), request.getStatus()));

        // Клик
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onRequestClick(request);
        });
    }

    // Форматирование даты дедлайна
    private String formatDeadline(String deadline) {
        return TextUtils.isEmpty(deadline) ? "Срок не указан" : "До: " + deadline;
    }

    // Текстовое представление статуса
    private String getStatusText(String status) {
        if (status == null) return "Новая";
        switch (status) {
            case "in_progress": return "В работе";
            case "completed": return "Завершена";
            default: return "Новая";
        }
    }

    // Цвет статуса
    private int getStatusColor(Context context, String status) {
        if (status == null) {
            return ContextCompat.getColor(context, R.color.gray);
        }
        switch (status) {
            case "completed":
                return ContextCompat.getColor(context, R.color.green);
            case "in_progress":
                return ContextCompat.getColor(context, R.color.orange);
            default:
                return ContextCompat.getColor(context, R.color.gray);
        }
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public void updateRequests(List<ClientRequest> newRequests) {
        this.requests = newRequests != null ? new ArrayList<>(newRequests) : new ArrayList<>();
        notifyDataSetChanged();
    }

    private String formatDate(long timestamp) {
        try {
            return new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                    .format(new Date(timestamp));
        } catch (Exception e) {
            return "дата неизвестна";
        }
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        final TextView requestText;
        final TextView descriptionText;
        final TextView deadlineText;
        final TextView statusText;

        RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            requestText = itemView.findViewById(R.id.requestText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            deadlineText = itemView.findViewById(R.id.deadlineText);
            statusText = itemView.findViewById(R.id.statusText);

            // Проверка на случай если разметка не содержит необходимых TextView
            if (requestText == null || descriptionText == null ||
                    deadlineText == null || statusText == null) {
                throw new IllegalStateException("Разметка элемента должна содержать все необходимые TextView");
            }
        }
    }
}