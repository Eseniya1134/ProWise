package com.example.ourpro.bottomnav.profile;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ourpro.R;
import com.example.ourpro.databinding.FragmentSecuritySettingsBinding;


public class SecuritySettingsFragment extends Fragment {

    private static final String TAG = "Upload ###";
    private FragmentSecuritySettingsBinding binding;
    private SharedPreferences sharedPreferences;


    public SecuritySettingsFragment() {
        super(R.layout.fragment_security_settings);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentSecuritySettingsBinding.bind(view);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        binding.profileText.setOnClickListener(v -> {
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.replace(R.id.menu_fr, new ProfileFragment());
            ft.commit();
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}