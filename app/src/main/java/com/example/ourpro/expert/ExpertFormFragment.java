package com.example.ourpro.expert;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.ourpro.PdfViewerFragment;
import com.example.ourpro.R;
import com.example.ourpro.bottomnav.profile.ProfileFragment;
import com.example.ourpro.databinding.FragmentExpertFormBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpertFormFragment extends Fragment {

    private String userId;
    private DatabaseReference rtdb;
    private FragmentExpertFormBinding binding;

    private String formId;
    private final int FILE_PICK_CODE = 1000;

    // Cloudinary настройки
    private final Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "ds9fvwury",
            "api_key", "315484973868967",
            "api_secret", "kB1ADn8BVHFCk5TPwoONIcj6u6U"
    ));


    private static final String TAG = "Upload ###";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExpertFormBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        formId = generateFormId();

        addExpert();
        addEducation();


        binding.save.setOnClickListener(v -> {
            saveFormExpert();
            addIDUrl(formId);
            navigateToProfile();
        });

        binding.profileButton.setOnClickListener(v -> {
            navigateToProfile();

        });
        saveFormExpert();
        return view;
    }

    private void navigateToProfile() {
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.replace(R.id.menu_fr, new ProfileFragment());
        ft.commit();
    }


    private void deleteFormID(){
        DatabaseReference formRef = FirebaseDatabase
                .getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference()
                .child("ExpertQuestionnaire")
                .child(userId)
                .child(formId);

        formRef.removeValue()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Форма полностью удалена"))
                .addOnFailureListener(e -> Log.e(TAG, "Ошибка удаления формы: " + e.getMessage()));

    }



    //Учет ID анкет
    private void addIDUrl(String newId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(requireContext(), "Пользователь не авторизован", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();

        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("Users")
                .child(userId)
                .child("idURLExpert"); // <- сюда сохраняем список ID

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String existingIds = snapshot.getValue(String.class);
                String updatedIds;

                if (existingIds != null && !existingIds.isEmpty()) {
                    // Проверка на дубликаты (по желанию)
                    if (!existingIds.contains(newId)) {
                        updatedIds = existingIds + "," + newId;
                    } else {
                        updatedIds = existingIds; // уже есть
                    }
                } else {
                    updatedIds = newId;
                }

                ref.setValue(updatedIds)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(requireContext(), "ID успешно добавлен", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(requireContext(), "Ошибка при сохранении ID", Toast.LENGTH_SHORT).show();
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Ошибка чтения из Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }



    /// РАБОТА С ВЫБОРОМ СПЕЦИАЛЬНОСТИ, УРОВНЕМ ОБРАЗОВАНИЯ, ОПЫТОМ РАБОТЫ И ОПИСАНИЕМ УСЛУГ
    //Метод для выборки специальности
    private void addExpert(){
        // Полный список профессий (в алфавитном порядке)
        List<String> professions = Arrays.asList(
                "Адвокат", "Аккаунт-менеджер", "Актёр", "Аналитик", "Архитектор",
                "Аудитор", "Бариста", "Бармен", "Биолог", "Бренд-менеджер",
                "Бухгалтер", "Веб-разработчик", "Видеооператор", "Военнослужащий", "Водитель",
                "Водитель-дальнобойщик", "Врач", "Графический дизайнер", "DevOps-инженер", "Дизайнер",
                "Журналист", "Инженер", "Инженер-конструктор", "Инженер-технолог", "Историк",
                "IT-консультант", "Каменщик", "Кардиолог", "Кассир", "Кондитер",
                "Контент-маркетолог", "Контент-менеджер", "Копирайтер", "Лингвист", "Литературный переводчик",
                "Логист", "Маркетолог", "Масажист", "Математик", "Менеджер",
                "Менеджер по продажам", "Методист", "Механик", "Мерчендайзер", "Мобильный разработчик",
                "Монтажёр", "Моушн-дизайнер", "Музыкант", "Нотариус", "Офис-менеджер",
                "Официант", "Офтальмолог", "Переводчик", "Пекарь", "Педагог-психолог",
                "Поэт", "Повар", "Политолог", "Полицейский", "Преподаватель",
                "Программист", "Продакт-менеджер", "Проект-менеджер", "Продавец", "Прокурор",
                "Писатель", "Плотник", "Пожарный", "Проектировщик", "QA-инженер",
                "Редактор", "Редактор перевода", "Репетитор", "Режиссёр", "Рерайтер",
                "Scrum-мастер", "SMM-менеджер", "SEO-специалист", "Синхронный переводчик", "Скульптор",
                "Социолог", "Спасатель", "Спортсмен", "Системный администратор", "Системный аналитик",
                "Стоматолог", "Строитель", "Су-шеф", "Таксист", "Тестировщик",
                "Терапевт", "Торговый представитель", "Тренер", "UX/UI-дизайнер", "Учитель",
                "Физик", "Финансовый аналитик", "Фитнес-инструктор", "Фотограф", "Химик",
                "Хирург", "Хостес", "HR-менеджер", "Эколог", "Экономист",
                "Электрик", "Энтомолог", "Юрист"
        );


        // Создаем адаптер с полным списком
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>(professions)
        );

        // Связываем с AutoCompleteTextView через биндинг
        binding.expert.setAdapter(adapter);
        binding.expert.setThreshold(1);

        // Добавление изображения
        binding.addImageButton.setOnClickListener(v -> openFilePicker());
    }

    //Метод выборки обучения
    private void addEducation(){
        List<String> educationLevels = Arrays.asList(
                "Колледж / техникум окончен", "Учусь в бакалавриате", "Бакалавр",
                "Учусь в магистратуре", "Магистр", "Учусь в аспирантуре",
                "Аспирант", "Кандидат наук", "Доктор наук",
                "Дополнительное образование / курсы"
        );



        // Создаем адаптер с полным списком
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>(educationLevels)
        );

        // Связываем с AutoCompleteTextView через биндинг
        binding.education.setAdapter(adapter2);
        binding.education.setThreshold(1);

        // Добавление изображения
        binding.addImageButton.setOnClickListener(v -> openFilePicker());
    }

    //Метод для сохранения в БД анкеты эксперта
    private String generateFormId() {
        return "form_" + System.currentTimeMillis(); // возвращает количество миллисекунд
    }

    // Метод для сохранения анкеты эксперта с уникальным ID
    private void saveFormExpert() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(requireContext(), "Пользователь не авторизован", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        String expert = binding.expert.getText().toString().trim();
        String education = binding.education.getText().toString().trim();
        String experience = binding.experienceGet.getText().toString().trim();
        String services = binding.servicesGet.getText().toString().trim();

        if (expert.isEmpty()) {
            Toast.makeText(requireContext(), "Специализация должна быть выбрана", Toast.LENGTH_SHORT).show();
            return;
        }

        if (education.isEmpty()) {
            Toast.makeText(requireContext(), "Уровень образования должен быть выбран", Toast.LENGTH_SHORT).show();
            return;
        }

        if (experience.isEmpty()){
            Toast.makeText(requireContext(), "Опыт работы должен быть заполнен", Toast.LENGTH_SHORT).show();
            return;
        }


        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference()
                .child("ExpertQuestionnaire")
                .child(userId)
                .child(formId);

        Map<String, Object> formData = new HashMap<>();
        formData.put("expert", expert);
        formData.put("education", education);
        formData.put("experience", experience);
        formData.put("services", services);
        formData.put("status", "");
        formData.put("timestamp", ServerValue.TIMESTAMP); // удобно для сортировки в будущем

        ref.updateChildren(formData)
                .addOnSuccessListener(aVoid ->
                        Log.d(TAG, "Анкета успешно сохранена: " + formId))
                .addOnFailureListener(e ->
                        Log.e(TAG, "Ошибка при сохранении анкеты: " + e.getMessage()));
    }



    /// РАБОТА С ДОКУМЕНТАМИ, ПОДТВЕРЖДАЮЩИЕ СПЕЦИАЛИЗАЦИЮ
    // Метод для выбора файла PDF через системный проводник
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Выберите PDF"), FILE_PICK_CODE);
    }

    // Обработка результата выбора файла
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_PICK_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri pdfUri = data.getData();
            addImageNameToContainer(pdfUri); // твоя функция, оставил как есть

            if (pdfUri != null) {
                uploadPdfToCloudinary(pdfUri, new UploadCallback() {
                    @Override
                    public void onSuccess(String fileUrl) {
                        saveFileUrlToFirebase(fileUrl);
                    }
                });
            }
        }
    }

    // Интерфейс callback для загрузки
    interface UploadCallback {
        void onSuccess(String fileUrl);
    }

    // Загрузка PDF в Cloudinary
    private void uploadPdfToCloudinary(Uri fileUri, UploadCallback callback) {
        new Thread(() -> {
            try {
                Map uploadResult = cloudinary.uploader().upload(
                        requireContext().getContentResolver().openInputStream(fileUri),
                        ObjectUtils.asMap("resource_type", "raw")
                );
                String fileUrl = (String) uploadResult.get("url");

                // Запуск в UI-потоке через Handler
                new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(fileUrl));
            } catch (Exception e) {
                Log.e(TAG, "Ошибка загрузки файла в Cloudinary: " + e.getMessage());

                // Показываем Toast в UI-потоке
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(requireContext(), "Ошибка загрузки файла", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    // Сохранение URL в Firebase
    private void saveFileUrlToFirebase(String fileUrl) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(requireContext(), "Пользователь не авторизован", Toast.LENGTH_SHORT).show();
            return;
        }

        userId = user.getUid();
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference()
                .child("ExpertQuestionnaire")
                .child(userId)
                .child(formId)
                .child("fileUrl");

        // Сначала получаем текущее значение
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String existingUrls = snapshot.getValue(String.class);

                String updatedUrls;
                if (existingUrls != null && !existingUrls.isEmpty()) {
                    updatedUrls = existingUrls + "," + fileUrl;
                } else {
                    updatedUrls = fileUrl;
                }

                ref.setValue(updatedUrls)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Файл успешно добавлен в список Firebase");
                            Toast.makeText(requireContext(), "Файл успешно загружен", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Ошибка сохранения файла: " + e.getMessage());
                            Toast.makeText(requireContext(), "Ошибка сохранения файла", Toast.LENGTH_SHORT).show();
                        });
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Ошибка чтения из Firebase: " + error.getMessage());
            }
        });
    }



    /// МЕТОД ДЛЯ ВЫГРУЗКИ В ИИ
    /*
    private void loadPDFForII(){

    }*/


    // Метод добавляет в интерфейс название выбранного файла и кнопку удаления
    private void addImageNameToContainer(Uri uri) {
        String imageName = getFileNameFromUri(uri);

        LinearLayout fileContainer = new LinearLayout(requireContext());
        fileContainer.setOrientation(LinearLayout.HORIZONTAL);
        fileContainer.setGravity(Gravity.CENTER_VERTICAL);

        // Текстовое поле с названием файла
        TextView textView = new TextView(requireContext());
        textView.setText(imageName);
        textView.setPadding(60, 8, 8, 8);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1
        );
        textView.setLayoutParams(textParams);

        // Кнопка удаления (крестик)
        ImageView deleteIcon = new ImageView(requireContext());
        deleteIcon.setImageResource(R.drawable.baseline_close_24);
        deleteIcon.setPadding(8, 8, 60, 8);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        deleteIcon.setLayoutParams(iconParams);

        deleteIcon.setOnClickListener(v -> binding.imageContainer.removeView(fileContainer));

        fileContainer.addView(textView);
        fileContainer.addView(deleteIcon);

        binding.imageContainer.addView(fileContainer);
    }

    // Метод извлекает имя файла из Uri (если возможно — из метаданных, иначе из пути)
    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            ContentResolver resolver = requireContext().getContentResolver();
            Cursor cursor = resolver.query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                if (nameIndex >= 0) {
                    result = cursor.getString(nameIndex);
                }
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}