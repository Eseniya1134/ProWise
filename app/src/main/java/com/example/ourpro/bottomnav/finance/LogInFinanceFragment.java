package com.example.ourpro.bottomnav.finance;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.ourpro.R;
import com.example.ourpro.databinding.FragmentLogInFinanceBinding;

public class LogInFinanceFragment extends Fragment {
    private ViewFlipper viewFlipper;
    private FragmentLogInFinanceBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentLogInFinanceBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.btnCreate.setOnClickListener(v -> {
            if (binding.viewFlipper.getDisplayedChild() < 1) {
                binding.viewFlipper.showNext();
            } else {
                Toast.makeText(getContext(), "Онбординг завершен!", Toast.LENGTH_SHORT).show();
                // переход на главный экран или сохранение состояния
            }
        });

        binding.btnCheck.setOnClickListener(v -> {
            if (binding.viewFlipper.getDisplayedChild() == 1) {
                binding.viewFlipper.showNext();
            }
        });

        binding.btnSave.setOnClickListener(v -> {
            if (binding.viewFlipper.getDisplayedChild() == 2) {
                binding.viewFlipper.showNext();
            }
        });

        binding.btnCheckPin.setOnClickListener(v -> {
            if (binding.viewFlipper.getDisplayedChild() == 3) {
                binding.viewFlipper.showNext();
            }
        });

        binding.btnNext.setOnClickListener(v -> {
            if (binding.viewFlipper.getDisplayedChild() == 4) {
                binding.viewFlipper.showNext();
            }
        });



        return view;
    }


    //Введение ПИНА

    private void PinGet(){


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}