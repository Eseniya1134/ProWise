package com.example.ourpro.bottomnav.catalog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourpro.R;
import com.example.ourpro.profile.UserProfileFragment;
import com.example.ourpro.databinding.ItemUserBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
            String email = user.getEmail();

            if (email == null || email.isEmpty()) {
                Log.e("UserAdapter", "Email пользователя пустой!");
                Toast.makeText(v.getContext(), "Ошибка: Email отсутствует", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("UserAdapter", "Ищем UID по email: " + email);

            FirebaseDatabase database = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app");
            DatabaseReference usersRef = database.getReference("Users");


            usersRef.orderByChild("email").equalTo(email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                    String userId = userSnapshot.getKey(); // <-- Получаем UID
                                    Log.d("UserAdapter", "Найден userId: " + userId);

                                    Bundle bundle = new Bundle();
                                    bundle.putString("userId", userId);

                                    UserProfileFragment fragment = new UserProfileFragment();
                                    fragment.setArguments(bundle);

                                    ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.menu_fr, fragment)
                                            .addToBackStack(null)
                                            .commit();

                                    break; // UID найден, дальше не ищем
                                }
                            } else {
                                Log.e("UserAdapter", "Пользователь с таким email не найден!");
                                Toast.makeText(v.getContext(), "Пользователь не найден", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("UserAdapter", "Ошибка запроса: " + error.getMessage());
                        }
                    });
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
