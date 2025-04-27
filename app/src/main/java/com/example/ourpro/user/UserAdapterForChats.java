package com.example.ourpro.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import com.example.ourpro.R;
import com.example.ourpro.utils.ChatUtil;
//import com.example.ourpro.utils.ChatUtil;

public class UserAdapterForChats extends RecyclerView.Adapter<UserViewHolder>{

    private ArrayList<User> users = new ArrayList<>();

    public UserAdapterForChats(ArrayList<User> users){
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);

        holder.username_tv.setText(user.getUsername());

        /*
        if (!user.profileImage.isEmpty()){
            Glide.with(holder.itemView.getContext()).load(user.profileImage).into(holder.profileImage_iv);
        }*/

        holder.itemView.setOnClickListener(view -> {
            ChatUtil.createChat(user);
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}