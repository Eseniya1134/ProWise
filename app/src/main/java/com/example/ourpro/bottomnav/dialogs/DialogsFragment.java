package com.example.ourpro.bottomnav.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.ourpro.bottomnav.dialogs.ViewPagerAdapter;

import com.example.ourpro.databinding.FragmentDialogsBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class DialogsFragment extends Fragment {

    private FragmentDialogsBinding binding;

    public DialogsFragment() {
        super();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDialogsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.viewPager.setSaveEnabled(false); //отключает попытку сохранить/восстановить состояние ViewPager2

        // Используй getChildFragmentManager() для корректного управления фрагментами внутри фрагмента
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(), getLifecycle());
        binding.viewPager.setAdapter(adapter);

        // Настройка TabLayoutMediator
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Чаты");
            } else {
                tab.setText("Звонки");
            }
        }).attach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
