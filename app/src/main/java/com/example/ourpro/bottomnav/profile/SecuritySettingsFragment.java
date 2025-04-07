package com.example.ourpro.bottomnav.profile;

import static com.google.android.material.textfield.TextInputLayout.END_ICON_NONE;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ourpro.R;
import com.example.ourpro.databinding.FragmentSecuritySettingsBinding;


public class SecuritySettingsFragment extends Fragment {

    public static final String ARG_SELECTED_VALUE = "selected_value";

    private FragmentSecuritySettingsBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Инфлейтим layout для этого фрагмента
        return inflater.inflate(R.layout.fragment_security_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentSecuritySettingsBinding.bind(view);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        // Получаем аргументы
        Bundle args = getArguments();


        if (args != null && args.containsKey(ARG_SELECTED_VALUE)) {
            int value = args.getInt(ARG_SELECTED_VALUE);

            switch (value) {
                case 1:
                    // Действия для смены пароля
                    binding.infText.setText("Точно ли хотите поменять пароль?");

                    break;
                case 2:
                    // Действия для смены email
                    binding.infText.setText("Точно ли хотите поменять почту?");
                    binding.newPassword.setHint("Новая почта");
                    binding.newPasswordRep.setVisibility(View.INVISIBLE);
                    binding.newPassword.setEndIconMode(END_ICON_NONE);
                    binding.newPasswordGet.setTransformationMethod(null);

                    break;
                case 3:
                    // Действия для смены имени пользователя
                    binding.infText.setText("Точно ли хотите поменять имя пользователя?");
                    binding.newPassword.setHint("Новое имя пользователя");
                    binding.newPasswordRep.setVisibility(View.INVISIBLE);
                    binding.newPassword.setEndIconMode(END_ICON_NONE);
                    binding.newPasswordGet.setTransformationMethod(null);

                    break;
            }
        } else {
        //    textView.setText("Значение не передано");
            Log.e("SecuritySettings", "Аргументы отсутствуют!");
        }
    }


}



