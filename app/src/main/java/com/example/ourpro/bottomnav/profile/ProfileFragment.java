package com.example.ourpro.bottomnav.profile;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    private Animation scaleAnimation, fadeInAnimation;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentProfileBinding.bind(view);

        // Инициализация анимаций
        scaleAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale);
        fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in);

        // Настройка поведения AppBar
        setupAppBar();

        // Анимация при открытии
        animateViewsOnCreate();

        // Обработчик кнопки
        binding.editProfileButton.setOnClickListener(v -> {
        //    v.startAnimation(scaleAnimation);
         //   v.postDelayed(this::navigateToAccountSettings, 200);
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.replace(R.id.menu_fr, new AccountSettingFragment());
            ft.commit();
        });
    }

    private void setupAppBar() {
        binding.appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int scrollRange = appBarLayout.getTotalScrollRange();
            float progress = -verticalOffset / (float) scrollRange;

            // Анимация прозрачности элементов
            binding.profileText.setAlpha(1 - progress);
            binding.exit.setAlpha(1 - progress);
            binding.settingButton.setAlpha(1 - progress);

            // Показ/скрытие мини-аватарки
            binding.profileImageSmall.setVisibility(progress > 0.7f ? View.VISIBLE : View.INVISIBLE);
            binding.settingsTitleSmall.setVisibility(progress > 0.7f ? View.VISIBLE : View.INVISIBLE);
        });
    }

    private void animateViewsOnCreate() {
        binding.profileImage.startAnimation(fadeInAnimation);
        binding.profileText.startAnimation(fadeInAnimation);

        binding.editProfileButton.postDelayed(() -> {
            binding.editProfileButton.startAnimation(fadeInAnimation);
        }, 150);
    }

    private void navigateToAccountSettings() {
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out_right
        );
        ft.replace(R.id.menu_fr, new AccountSettingFragment());
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (scaleAnimation != null) scaleAnimation.cancel();
        if (fadeInAnimation != null) fadeInAnimation.cancel();
        binding = null;
    }
}