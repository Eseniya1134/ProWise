
package com.example.ourpro.bottomnav.catalog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ourpro.databinding.FragmentSearchBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListOfUsersFragment extends Fragment {

    private FragmentSearchBinding binding;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();
    private String query = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Получаем строку запроса
        if (getArguments() != null) {
            query = getArguments().getString("query", "").toLowerCase();
        }

        setupRecyclerView();
        searchUsers(query);
    }

    private void setupRecyclerView() {
        binding.searchsWindow.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new UserAdapter(userList);
        binding.searchsWindow.setAdapter(userAdapter);
    }

    private void searchUsers(String query) {
        binding.progressBar.setVisibility(View.VISIBLE);

        DatabaseReference usersRef = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("Users");

        usersRef.orderByChild("username")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userList.clear();

                        for (DataSnapshot userSnap : snapshot.getChildren()) {
                            User user = userSnap.getValue(User.class);
                            if (user != null) {
                                userList.add(user);
                            }
                        }

                        binding.progressBar.setVisibility(View.GONE);

                        if (userList.isEmpty()) {
                            binding.emptyMessage.setVisibility(View.VISIBLE);
                        } else {
                            binding.emptyMessage.setVisibility(View.GONE);
                        }

                        userAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

