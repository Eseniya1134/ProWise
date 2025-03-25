package com.example.ourpro.bottomnav.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ourpro.R;
import com.example.ourpro.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {
    public ProfileFragment() {
        super(R.layout.fragment_profile);
    }

    private FragmentProfileBinding binding;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentProfileBinding.bind(view);

        binding.editingAccount.setOnClickListener( v -> {
            Toast.makeText(requireContext(), "Редактирование профиля", Toast.LENGTH_SHORT).show();
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.replace(R.id.menu_fr, new AccountSettingFragment());
            ft.addToBackStack(null);
            ft.commit();
        });


    }
}
