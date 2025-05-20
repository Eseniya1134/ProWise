package com.example.ourpro.bottomnav.profile;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.example.ourpro.requests.ClientRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ClientRequestFragment extends Fragment {
    private FragmentClientRequestBinding binding;
    private ClientRequest currentRequest;
    private boolean isEditMode = false;
    private Calendar selectedDate = Calendar.getInstance();
    private static final String TAG = "ClientRequestFragment";

    public static ClientRequestFragment newInstance(ClientRequest request) {
        ClientRequestFragment fragment = new ClientRequestFragment();
        Bundle args = new Bundle();
        args.putParcelable("request", (Parcelable) request);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentClientRequestBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app");
        Log.d(TAG, "Database URL: " + database.getReference().toString());

        Log.d(TAG, "Firebase init check: " + FirebaseDatabase.getInstance().getReference());
        if (getArguments() != null) {
            currentRequest = getArguments().getParcelable("request");
            isEditMode = true;
            fillFormWithData();
        }
        setupForm();


        binding.profileButton.setOnClickListener(v -> {
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.replace(R.id.menu_fr, new ProfileFragment());
            ft.commit();
        });
        return view;

    }

    private void setupForm() {
        String[] domains = {"Медицина и здравоохранение", "Юриспруденция и правопорядок", " IT и программирование", " Маркетинг и коммуникации", " Искусство и творчество", " Наука и исследования", "Образование", "Бизнес и управление", "Транспорт и логистика", "Питание и гостеприимство", "Финансы и бухгалтерия", "Строительство и инженерия"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                domains
        );
        binding.problemDomain.setAdapter(adapter);
        binding.deadline.setOnClickListener(v -> showDatePicker());

        binding.submitButton.setOnClickListener(v -> {
            // Добавляем проверку перед сохранением

            if (validateForm()) {
                saveProblemToFirebase();
            }
        });
    }

    private boolean validateForm() {
        boolean isValid = true;

        if (binding.problemDomain.getText().toString().trim().isEmpty()) {
            binding.problemDomain.setError("Обязательное поле");
            isValid = false;
        }

        if (binding.deadline.getText().toString().trim().isEmpty()) {
            binding.deadline.setError("Укажите срок выполнения");
            isValid = false;
        }

        return isValid;
    }

    private void showDatePicker() {
        new DatePickerDialog(
                requireContext(),
                (view, year, month, day) -> {
                    selectedDate.set(year, month, day);
                    String formattedDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                            .format(selectedDate.getTime());
                    binding.deadline.setText(formattedDate);
                    binding.deadline.setError(null); // Очищаем ошибку, если была
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) requireContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private void saveProblemToFirebase() {
        if (!isNetworkAvailable()) {
            Toast.makeText(getContext(), "Нет интернет-соединения", Toast.LENGTH_LONG).show();
            binding.submitButton.setEnabled(true);
            binding.submitButton.setText("Сохранить");
            return;
        }

        // Проверка заполнения полей
        if (binding.problemDomain.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Заполните обязательные поля", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "Необходимо авторизоваться", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.submitButton.setEnabled(false);
        binding.submitButton.setText("Сохранение...");

        // Подготовка данных
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("domain", binding.problemDomain.getText().toString().trim());
        requestData.put("shortDescription", binding.shortDescription.getText().toString().trim());
        requestData.put("fullDescription", binding.fullDescription.getText().toString().trim());
        requestData.put("deadline", binding.deadline.getText().toString().trim());
        requestData.put("timestamp", ServerValue.TIMESTAMP);
        requestData.put("status", "new");

        // Сохранение в Firebase
        DatabaseReference ref = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("ClientRequests")
                .child(user.getUid())
                .push();

        ref.setValue(requestData)
                .addOnCompleteListener(task -> {
                    binding.submitButton.setEnabled(true);
                    binding.submitButton.setText("Сохранить");

                    if (task.isSuccessful()) {
                        Log.d(TAG, "Данные успешно сохранены в Firebase");
                        clearForm();
                        requireActivity().onBackPressed();
                    } else {
                        Log.e(TAG, "Ошибка сохранения", task.getException());
                        Toast.makeText(getContext(),
                                "Ошибка сохранения: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
        Log.d(TAG, "Пытаемся сохранить: " + requestData.toString());
    }

    private void fillFormWithData() {
        if (currentRequest != null) {
            binding.problemDomain.setText(currentRequest.getDomain());
            binding.shortDescription.setText(currentRequest.getShortDescription());
            binding.fullDescription.setText(currentRequest.getFullDescription());
            binding.deadline.setText(currentRequest.getDeadline());

            // Если это режим просмотра, делаем поля недоступными для редактирования
            if (!isEditMode) {
                binding.problemDomain.setEnabled(false);
                binding.shortDescription.setEnabled(false);
                binding.fullDescription.setEnabled(false);
                binding.deadline.setEnabled(false);
                binding.submitButton.setVisibility(View.GONE);
            }
        }
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