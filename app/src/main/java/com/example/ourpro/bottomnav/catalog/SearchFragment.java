package com.example.ourpro.bottomnav.catalog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ourpro.databinding.FragmentSearchBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;

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

        setupViewPager();      // <-- добавлено
        setupTabs();
        setupCancelButton();
    }

    private void setupViewPager() {
        binding.searchsWindow.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return position == 0 ? new ListOfUsersFragment() : (position == 1 ? new ConsultationsFragment() : new TagsFragment());
            }

            @Override
            public int getItemCount() {
                return 3;
            }
        });
    }

    private void setupTabs() {

        new TabLayoutMediator(binding.tabLayout, binding.searchsWindow, (tab, position) -> {
            tab.setText(position == 0 ? "Пользователи" : (position ==1 ? "Консультации" : "Теги"));
        }).attach();
    }


    private void setupCancelButton() {
        binding.cancelButton.setOnClickListener(v -> binding.search.setText(""));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
