package com.example.ourpro.bottomnav.profile;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ourpro.R;
import com.example.ourpro.databinding.FragmentClientRequestBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ClientRequestFragment extends Fragment {
    private FragmentClientRequestBinding binding;
    private Calendar selectedDate = Calendar.getInstance();
    private static final String TAG = "ClientRequestFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentClientRequestBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Log.d(TAG, "Firebase init check: " + FirebaseDatabase.getInstance().getReference());
        setupForm();

        binding.profileButton.setOnClickListener(v -> {
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.replace(R.id.menu_fr, new ProfileFragment());
            ft.commit();
        });
        return view;
    }

    private void setupForm() {
        String[] domains = {"Технологии", "Здоровье", "Образование", "Финансы", "Другое"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                domains
        );
        binding.problemDomain.setAdapter(adapter);
        binding.deadline.setOnClickListener(v -> showDatePicker());
        // Отправка данных
        binding.submitButton.setOnClickListener(v -> saveProblemToFirebase());
    }

    private void showDatePicker() {
        new DatePickerDialog(
                requireContext(),
                (view, year, month, day) -> {
                    selectedDate.set(year, month, day);
                    binding.deadline.setText(new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(selectedDate.getTime()));
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void saveProblemToFirebase() {
        //Проверка заполнения обязательных полей
        if (binding.problemDomain.getText().toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Укажите сферу проблемы", Toast.LENGTH_SHORT).show();
            binding.problemDomain.requestFocus();
            return;
        }

        if (binding.shortDescription.getText().toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Введите краткое описание", Toast.LENGTH_SHORT).show();
            binding.shortDescription.requestFocus();
            return;
        }

        //Получаем текущего пользователя
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(requireContext(), "Ошибка: пользователь не авторизован", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Пользователь не авторизован при попытке сохранения");
            return;
        }

        String userId = user.getUid();

        //Блокируем кнопку для предотвращения повторных нажатий
        binding.submitButton.setEnabled(false);
        binding.submitButton.setText("Сохранение...");

        //Подготовка данных
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("domain", binding.problemDomain.getText().toString().trim());
        requestData.put("shortDescription", binding.shortDescription.getText().toString().trim());
        requestData.put("fullDescription", binding.fullDescription.getText().toString().trim());
        requestData.put("deadline", binding.deadline.getText().toString().trim());
        //requestData.put("timestamp", System.currentTimeMillis()); // Добавляем timestamp для сортировки

        //Получаем ссылку на базу данных
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference userRequestsRef = database.getReference("ClientRequests").child(userId);

        //Сохраняем данные под узлом пользователя
        String requestId = userRequestsRef.push().getKey();
        if (requestId == null) {
            Toast.makeText(requireContext(), "Ошибка создания запроса", Toast.LENGTH_SHORT).show();
            binding.submitButton.setEnabled(true);
            binding.submitButton.setText("Сохранить");
            return;
        }

        userRequestsRef.child(requestId).setValue(requestData)
                .addOnCompleteListener(task -> {
                    binding.submitButton.setEnabled(true);
                    binding.submitButton.setText("Сохранить");

                    if (task.isSuccessful()) {
                        Toast.makeText(requireContext(), "Запрос успешно сохранен", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Документ запроса сохранен с ID: " + requestId + " для пользователя " + userId);
                        clearForm();

                        // Возвращаемся назад после успешного сохранения
                        requireActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(requireContext(), "Ошибка сохранения", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Ошибка сохранения запроса", task.getException());
                    }
                });
    }

    private void clearForm() {
        binding.problemDomain.setText("");
        binding.shortDescription.setText("");
        binding.fullDescription.setText("");
        binding.deadline.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}