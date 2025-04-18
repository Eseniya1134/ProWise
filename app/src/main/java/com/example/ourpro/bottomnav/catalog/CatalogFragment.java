package com.example.ourpro.bottomnav.catalog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ourpro.databinding.FragmentCatalogBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CatalogFragment extends Fragment {

    private FragmentCatalogBinding binding;
    private UserAdapter userAdapter;
    private DatabaseReference usersRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCatalogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        userAdapter = new UserAdapter();
        binding.recyclerViewResults.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewResults.setAdapter(userAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app/");
        usersRef = database.getReference("users");

        binding.buttonSearch.setOnClickListener(v -> {
            String searchQuery = binding.editTextNickname.getText().toString().toLowerCase();
            Log.d("CatalogFragment", "Поиск по запросу: " + searchQuery);
            fetchUsers(searchQuery);
        });

        return view;
    }

    private void fetchUsers(String searchQuery) {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> result = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    if (user != null && user.getUsername() != null) {
                        Log.d("CatalogFragment", "Пользователь найден: " + user.getUsername());
                        if (user.getUsername().toLowerCase().contains(searchQuery)) {
                            result.add(user);
                        }
                    }
                }
                Log.d("CatalogFragment", "Результаты поиска: " + result.size());
                userAdapter.setUserList(result);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CatalogFragment", "Ошибка при загрузке данных: " + error.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}


