package com.example.ourpro.bottomnav.catalog;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ourpro.R;
import com.example.ourpro.databinding.FragmentCatalogBinding;

public class CatalogFragment extends Fragment {
    public CatalogFragment() {
        super(R.layout.fragment_catalog);
    }

    private FragmentCatalogBinding binding;
    private UserAdapter userAdapter;
    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCatalogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        userAdapter = new UserAdapter();
        databaseHelper = new DatabaseHelper(getContext());

        binding.recyclerViewResults.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewResults.setAdapter(userAdapter);

        binding.buttonSearch.setOnClickListener(v -> {
            String nickname = binding.editTextNickname.getText().toString();
            List<User> users = databaseHelper.getUsersByNickname(nickname);
            userAdapter.setUserList(users);
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


