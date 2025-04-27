package com.example.ourpro.user;




import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import com.example.ourpro.R;

    public class UserViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileImage_iv;
        TextView username_tv;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

           // profileImage_iv = itemView.findViewById(R.id.profile_iv);
            username_tv = itemView.findViewById(R.id.username_tv);
        }
    }
