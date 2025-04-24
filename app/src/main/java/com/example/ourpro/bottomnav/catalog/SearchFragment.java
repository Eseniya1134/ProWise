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
        setupViewPager();
    }

    private void setupViewPager() {
        String query = getArguments() != null ? getArguments().getString("query", "") : "";

        binding.viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                Bundle args = new Bundle();
                args.putString("query", query);

                if (position == 0) {
                    ListOfUsersFragment fragment = new ListOfUsersFragment();
                    fragment.setArguments(args);
                    return fragment;
                } else if (position == 1) {
                    return new ConsultationsFragment(); // Здесь можно добавить фрагмент для консультаций
                } else {
                    return new TagsFragment(); // И для тегов
                }
            }

            @Override
            public int getItemCount() {
                return 3;
            }
        });

        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    if (position == 0) tab.setText("Пользователи");
                    else if (position == 1) tab.setText("Консультации");
                    else tab.setText("Теги");
                }
        ).attach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
