package com.example.ourpro.bottomnav.catalog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourpro.R;
import com.example.ourpro.bottomnav.profile.AccountSettingFragment;
import com.example.ourpro.databinding.ItemUserBinding;
import com.example.ourpro.profile.UserProfileFragment;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<User> userList;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding binding = ItemUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.binding.userName.setText(user.getUsername());
        holder.binding.userEmail.setText(user.getEmail());

        // Обработка нажатия на item
        holder.binding.getRoot().setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("userId", user.getUid()); // UID — уникальный ID пользователя в Firebase

            UserProfileFragment fragment = new UserProfileFragment();
            fragment.setArguments(bundle);

            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.menu_fr, new UserProfileFragment()) // Заменяем текущий фрагмент
                    .addToBackStack(null) // Добавляем в back stack для возможности возврата
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ItemUserBinding binding;

        public UserViewHolder(@NonNull ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
