package com.example.ourpro.bottomnav.catalog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

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
    private DatabaseReference usersRef;
    private List<String> userNamesList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private List<User> users = new ArrayList<>();
    private UserAdapter userAdapter; // твой кастомный адаптер для RecyclerView

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCatalogBinding.inflate(inflater, container, false);

        usersRef = FirebaseDatabase
                .getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users");

        setupAutoComplete();
        setupRecyclerView();
        setupSearchButton();

        return binding.getRoot();
    }

    private void setupAutoComplete() {
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, userNamesList);
        binding.autoCompleteSearch.setAdapter(adapter);
        binding.autoCompleteSearch.setThreshold(1); // с какого символа начинать показывать подсказки

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userNamesList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    if (user != null && user.getUsername() != null) {
                        userNamesList.add(user.getUsername());
                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void setupRecyclerView() {
        userAdapter = new UserAdapter(users);
        binding.recyclerResults.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerResults.setAdapter(userAdapter);
    }

    private void setupSearchButton() {
        binding.btnSearch.setOnClickListener(v -> {
            String query = binding.autoCompleteSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                searchUsers(query);
            }
        });
    }

    private void searchUsers(String query) {
        usersRef.orderByChild("username")
                .startAt(query)
                .endAt(query + "\uf8ff")// Для поиска с префиксом
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        users.clear();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            User user = child.getValue(User.class);
                            if (user != null) {
                                users.add(user);
                            }
                        }
                        userAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
