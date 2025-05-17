package com.example.ourpro.expert;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourpro.R;
import com.google.firebase.database.*;

import java.util.ArrayList;

// ExpertAdapter class
public class ExpertAdapter extends RecyclerView.Adapter<ExpertViewHolder> {

    private final ArrayList<Expert> experts;
    private final DatabaseReference rtdb;

    public ExpertAdapter(ArrayList<Expert> experts) {
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
        holder.confirmation_txt.setText("Подтверждено");

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

    }

    @Override
    public int getItemCount() {
        return experts.size();
    }
}
