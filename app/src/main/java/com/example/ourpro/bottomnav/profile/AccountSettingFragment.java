package com.example.ourpro.bottomnav.profile;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.example.ourpro.R;
import com.example.ourpro.databinding.FragmentAccountSettingBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class AccountSettingFragment extends Fragment {


    public AccountSettingFragment() {
        super(R.layout.fragment_account_setting);
    }
    private FragmentAccountSettingBinding binding;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Выбор пола
        String[] items = {"Выберите пол", "Мужской", "Женский"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                items
        );
        binding.gender.setAdapter(adapter);


        //Выбор дня рождения
        binding.birthdayText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (DatePicker view1, int selectedYear, int selectedMonth, int selectedDay) -> {
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        binding.birthdayText.setText(selectedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        binding.avatar.setOnClickListener(v -> {

        });
    }
}