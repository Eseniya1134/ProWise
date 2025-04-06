package com.example.ourpro.bottomnav.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ourpro.R;



public class SecuritySettingsFragment extends Fragment {

    public static final String ARG_SELECTED_VALUE = "selected_value";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Инфлейтим layout для этого фрагмента
        return inflater.inflate(R.layout.fragment_security_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Находим TextView в layout
        TextView textView = view.findViewById(R.id.textView);

        // Получаем аргументы
        Bundle args = getArguments();

        if (args != null && args.containsKey(ARG_SELECTED_VALUE)) {
            int value = args.getInt(ARG_SELECTED_VALUE);
            textView.setText("Получено значение: " + value);

            // Здесь можно добавить логику в зависимости от значения
            switch (value) {
                case 1:
                    // Действия для смены пароля
                    break;
                case 2:
                    // Действия для смены email
                    break;
                case 3:
                    // Действия для смены имени пользователя
                    break;
            }
        } else {
            textView.setText("Значение не передано");
            Log.e("SecuritySettings", "Аргументы отсутствуют!");
        }
    }
}



