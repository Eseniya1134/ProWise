package com.example.ourpro.chats;


import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourpro.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatViewHolder extends RecyclerView.ViewHolder{

    CircleImageView chat_iv;
    TextView chat_name_tv;

    TextView userchat_tv;

    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);

        userchat_tv = itemView.findViewById(R.id.name_tv);
        chat_iv = itemView.findViewById(R.id.profile_iv);
        chat_name_tv = itemView.findViewById(R.id.username_tv);

    }
}