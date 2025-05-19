package com.example.ourpro.expert;

import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourpro.R;
import com.example.ourpro.chats.Chat;
import com.example.ourpro.chats.ChatViewHolder;
import com.example.ourpro.user.UserItemFullFormFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

// ExpertAdapter class
public class ExpertAdapter extends RecyclerView.Adapter<ExpertViewHolder> {

    private final ArrayList<Expert> experts;
    private String userId;
    private final DatabaseReference rtdb;

    public ExpertAdapter(ArrayList<Expert> experts, String userId) {
        this.userId = userId;
        this.experts = experts;
        this.rtdb = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app").getReference();
    }

    @NonNull
    @Override
    public ExpertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_form_expert, parent, false);
        return new ExpertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpertViewHolder holder, int position) {
        Expert expert = experts.get(position);
        holder.expert_txt.setText(expert.getExpert());
        holder.confirmation_txt.setText("Не подтверждено");
        Log.d("ExpertAdapter", "Категория: " + expert.getCategory());

        bindImage(holder, expert.getCategory());

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (userId.equals(uid)){
            holder.itemView.setOnClickListener(v -> {
                Fragment fragment = new ItemFullFormFragment();
                Bundle args = new Bundle();
                args.putString("expertId", expert.getExpertId());
                fragment.setArguments(args);

                // Проверка на FragmentActivity
                if (holder.itemView.getContext() instanceof FragmentActivity) {
                    FragmentActivity fragmentActivity = (FragmentActivity) holder.itemView.getContext();
                    fragmentActivity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.menu_fr, fragment) // Замените на ваш контейнер
                            .addToBackStack(null)
                            .commit();
                }
            });
        } else {
            holder.itemView.setOnClickListener(v -> {
                Fragment fragment = new UserItemFullFormFragment();
                Bundle args = new Bundle();
                args.putString("expertId", expert.getExpertId());
                args.putString("userId", userId);
                fragment.setArguments(args);

                // Проверка на FragmentActivity
                if (holder.itemView.getContext() instanceof FragmentActivity) {
                    FragmentActivity fragmentActivity = (FragmentActivity) holder.itemView.getContext();
                    fragmentActivity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.menu_fr, fragment) // Замените на ваш контейнер
                            .addToBackStack(null)
                            .commit();
                }
            });
        }




    }

    private void bindImage(@NonNull ExpertViewHolder holder, String category) {
        if (category == null) return;

        switch (category) {
            case "Искусство и творчество":
                holder.imageView.setImageResource(R.drawable.art);
                break;
            case "Бизнес и управление":
                holder.imageView.setImageResource(R.drawable.business);
                break;
            case "Строительство и инженерия":
                holder.imageView.setImageResource(R.drawable.construction);
                break;
            case "Образование":
                holder.imageView.setImageResource(R.drawable.education);
                break;
            case "Финансы и бухгалтерия":
                holder.imageView.setImageResource(R.drawable.finance);
                break;
            case "Питание и гостеприимство":
                holder.imageView.setImageResource(R.drawable.food);
                break;
            case "IT и программирование":
                holder.imageView.setImageResource(R.drawable.it);
                break;
            case "Юриспруденция и правопорядок":
                holder.imageView.setImageResource(R.drawable.law);
                break;
            case "Маркетинг и коммуникации":
                holder.imageView.setImageResource(R.drawable.marketing);
                break;
            case "Медицина и здравоохранение":
                holder.imageView.setImageResource(R.drawable.medicine);
                break;
            case "Наука и исследования":
                holder.imageView.setImageResource(R.drawable.science);
                break;
            case "Транспорт и логистика":
                holder.imageView.setImageResource(R.drawable.transport);
                break;
        }
    }


    @Override
    public int getItemCount() {
        return experts.size();
    }
}
