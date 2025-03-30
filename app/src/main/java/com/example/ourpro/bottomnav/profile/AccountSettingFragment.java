package com.example.ourpro.bottomnav.profile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.ourpro.R;
import com.example.ourpro.databinding.FragmentAccountSettingBinding;
import com.squareup.picasso.Picasso;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AccountSettingFragment extends Fragment {

    private static final String TAG = "Upload ###";
    private static final String PREF_IMAGE_URL = "saved_image_url";
    private FragmentAccountSettingBinding binding;
    private Uri imagePath;
    private SharedPreferences sharedPreferences;

    private String selectedDate;

    public AccountSettingFragment() {
        super(R.layout.fragment_account_setting);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentAccountSettingBinding.bind(view);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        initConfig(); // Настройка конфигурации Cloudinary
        loadBirthday();
        loadUserImageFromFirebase(); // Загружаем сохранённое изображение


        // Настройка выпадающего списка пола
        String[] items = {"Выберите пол", "Мужской", "Женский"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                items
        );

        binding.gender.setAdapter(adapter);

        // Выбор дня рождения
        binding.birthdayText.setOnClickListener(v -> selectBirthday());

        // Обработка нажатия на аватар для выбора изображения
        binding.avatar.setOnClickListener(v -> selectImage());

        // Обработка нажатия на кнопку загрузки
        binding.save.setOnClickListener(v -> {
            uploadImage();
            saveBirthday();
        });
    }


    //МЕТОДЫ ДАТЫ РОЖДЕНИЯ
    //метод загрузки даты рождения
    private void loadBirthday() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e(TAG, "Ошибка: пользователь не найден");
            return;
        }

        String userId = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference databaseReference = database.getReference("Users").child(userId).child("dateOfBirth");

        //       Log.d(TAG, "Запрос к Firebase по пути: " + databaseReference.getPath());

        databaseReference.get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                selectedDate = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Дата из Firebase: " + selectedDate);

                if (binding.birthdayText != null) {
                    binding.birthdayText.postDelayed(() -> {
                        binding.birthdayText.setText(selectedDate);
                        Log.d(TAG, "Текст установлен в UI: " + selectedDate);
                    }, 100);
                } else {
                    Log.e(TAG, "Ошибка: binding.birthdayText == null");
                }
            } else {
                Log.d(TAG, "Дата в Firebase отсутствует или пустая");
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Ошибка загрузки: " + e.getMessage()));
    }

    // метод сохранения даты рождения
    private void saveBirthday(){
        // Получаем текущего пользователя
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app");
            DatabaseReference databaseReference = database.getReference("Users");

            Map<String, Object> userUpdates = new HashMap<>();
            userUpdates.put("dateOfBirth", getSelectBirhday());

            // Обновляем данные в Firebase
            String userId = user.getUid();
            databaseReference.child(userId).updateChildren(userUpdates)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "дата рождения сохранена в Firebase"))
                    .addOnFailureListener(e -> Log.e(TAG, "Ошибка сохранения в Firebase: " + e.getMessage()));
        }
    }

    // Метод возващения даты рождения
    private String getSelectBirhday(){
        return selectedDate;
    }

    // метод выборки даты рождения
    private void selectBirthday(){

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (DatePicker view1, int selectedYear, int selectedMonth, int selectedDay) -> {
                    selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    binding.birthdayText.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }




    //МЕТОДЫ ВЫГРУЗКИ АВАТАРОК
    // метод выгрузки в настройках
    private void loadUserImageFromFirebase() {
        // Получаем текущего пользователя
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Проверяем, авторизован ли пользователь
        if (user != null) {
            // Получаем URL для подключения к Firebase Database
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app");
            DatabaseReference databaseReference = database.getReference("Users");  // Получаем ссылку на "Users" в Firebase

            String userId = user.getUid(); // Загружаем данные пользователя
            databaseReference.child(userId).child("profileImageURL").get()
                    .addOnSuccessListener(dataSnapshot -> {
                        // Проверяем, существует ли ссылка на изображение
                        if (dataSnapshot.exists()) {
                            // Получаем ссылку на изображение
                            String imageUrl = dataSnapshot.getValue(String.class);
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Log.d(TAG, "Загружается изображение из Firebase: " + imageUrl);
                                // Загружаем изображение в ImageView с помощью Picasso
                                Picasso.get().load(imageUrl).into(binding.avatar);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Логируем ошибку в случае неудачной попытки загрузки данных
                        Log.e(TAG, "Ошибка загрузки изображения из Firebase: " + e.getMessage());
                    });
        } else {
            // Если пользователь не авторизован
            Log.d(TAG, "Пользователь не авторизован.");
        }
    }

    // Метод для загрузки изображения на Cloudinary
    private void uploadImage() {
        try {
            // Проверяем, был ли уже инициализирован MediaManager
            if (MediaManager.get() == null) {
                throw new IllegalStateException("MediaManager не инициализирован");
            }
        } catch (IllegalStateException e) {
            // Если MediaManager не был инициализирован, инициализируем его
            Map<String, Object> config = new HashMap<>();
            config.put("cloud_name", "ds9fvwury");
            config.put("api_key", "315484973868967");
            config.put("api_secret", "kB1ADn8BVHFCk5TPwoONIcj6u6U");

            MediaManager.init(requireContext(), config);
            Log.d(TAG, "Cloudinary инициализирован заново");
        }

        if (imagePath == null) {
            Log.e(TAG, "Ошибка: нет выбранного изображения");
            Toast.makeText(requireContext(), "Ошибка: выберите изображение", Toast.LENGTH_SHORT).show();
            return;
        }

        MediaManager.get().upload(imagePath).callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {
                Log.d(TAG, "Загрузка началась");
                Toast.makeText(requireContext(), "Начало загрузки", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {
                Log.d(TAG, "Загрузка в процессе...");
            }

            @Override
            public void onSuccess(String requestId, Map resultData) {
                String imageUrl = resultData.get("secure_url").toString();
                Log.d(TAG, "Загрузка завершена: " + imageUrl);

                // Сохраняем в SharedPreferences (для локального кэширования)
                sharedPreferences.edit().putString(PREF_IMAGE_URL, imageUrl).apply();

                // Загружаем в ImageView
                Picasso.get().load(imageUrl).into(binding.avatar);

                // Получаем текущего пользователя
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                // Если пользователь авторизован, сохраняем ссылку в Firebase
                if (user != null) {
                    // Получаем URL для подключения к Firebase Database (если у вас есть кастомный URL)
                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app");

                    // Получаем ссылку на "Users" в Firebase
                    DatabaseReference databaseReference = database.getReference("Users");

                    // Создаем мапу с данными для обновления в Firebase
                    Map<String, Object> userUpdates = new HashMap<>();
                    userUpdates.put("profileImageURL", imageUrl);  // Сохраняем ссылку на изображение

                    // Обновляем данные в Firebase
                    String userId = user.getUid();
                    databaseReference.child(userId).updateChildren(userUpdates)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Ссылка сохранена в Firebase"))
                            .addOnFailureListener(e -> Log.e(TAG, "Ошибка сохранения в Firebase: " + e.getMessage()));
                }

                // Показать сообщение об успешной загрузке
                Toast.makeText(requireContext(), "Загрузка завершена", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
                Log.e(TAG, "Ошибка загрузки: " + error.getDescription());
                Toast.makeText(requireContext(), "Ошибка загрузки: " + error.getDescription(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {
                Log.w(TAG, "Загрузка отложена: " + error.getDescription());
                Toast.makeText(requireContext(), "Загрузка отложена", Toast.LENGTH_SHORT).show();
            }
        }).dispatch();
    }

    // Метод для выбора изображения из галереи
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }





    // Инициализация конфигурации Cloudinary
    private void initConfig() {
        try {
            if (MediaManager.get() == null) { // Попытка доступа к MediaManager
                Map<String, Object> config = new HashMap<>();
                config.put("cloud_name", "ds9fvwury");
                config.put("api_key", "315484973868967");
                config.put("api_secret", "kB1ADn8BVHFCk5TPwoONIcj6u6U");
                MediaManager.init(requireContext(), config);
                Log.d(TAG, "Cloudinary инициализирован");
            }
        } catch (Exception e) {
            Log.d(TAG, "Cloudinary уже был инициализирован или ошибка: " + e.getMessage());
        }
    }

    // Лаунчер для обработки результата выбора изображения
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        imagePath = result.getData().getData();
                        Log.d(TAG, "Выбранный путь изображения: " + imagePath);

                        if (imagePath != null) {
                            Picasso.get().load(imagePath).into(binding.avatar);
                        } else {
                            Toast.makeText(requireContext(), "Ошибка выбора изображения", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}