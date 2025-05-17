package com.example.ourpro.bottomnav.profile;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourpro.R;

public class ExpertViewHolder extends RecyclerView.ViewHolder {

    TextView confirmation_txt;

    TextView expert_txt;

    public ExpertViewHolder(@NonNull View itemView) {
        super(itemView);

        expert_txt= itemView.findViewById(R.id.expert_txt);
        confirmation_txt = itemView.findViewById(R.id.confirmation_txt);

    }
}
