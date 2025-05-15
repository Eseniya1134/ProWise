package com.example.ourpro.bottomnav.profile;

import static java.lang.String.join;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ourpro.R;
import com.example.ourpro.SignActivity;
import com.example.ourpro.bottomnav.catalog.ConsultationsFragment;
import com.example.ourpro.bottomnav.catalog.ListOfUsersFragment;
import com.example.ourpro.bottomnav.catalog.TagsFragment;
import com.example.ourpro.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.concurrent.atomic.AtomicReference;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        super(R.layout.fragment_profile);
    }
    private static final String TAG = "Upload ###";

    private FragmentProfileBinding binding;
    private Animation scaleAnimation, fadeInAnimation;

    private Button addBtn;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentProfileBinding.bind(view);
        loadFullName();
        loadGenderANDdob();
        loadUserName();
        loadUserImageToProfile();
        loadUserInfo();
        setupViewPager();

        binding.settingText.setOnClickListener(v -> {
            navigateToAccountSettings();
        });
        binding.settingText2.setOnClickListener(v -> {
            navigateToAccountSettings();
        });
        //binding.exit.setOnClickListener(v -> logout());
        //binding.exit2.setOnClickListener(v -> logout());

        // Инициализация анимаций
        scaleAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale);
        fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in);

        // Настройка поведения AppBar
        setupAppBar();

        // Анимация при открытии
        animateViewsOnCreate();

        // Находим кнопку "Добавить" и настраиваем обработчик
        addBtn = view.findViewById(R.id.addBtn);
        addBtn.setOnClickListener(v -> openClientRequestForm());

    }


    private void setupAppBar() {
        binding.appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int scrollRange = appBarLayout.getTotalScrollRange();
            float progress = -verticalOffset / (float) scrollRange;

            // Анимация прозрачности элементов
            //binding.exit.setAlpha(1 - progress);
            binding.settingText.setAlpha(1 - progress);
            binding.fullName.setAlpha(1 - progress);
            binding.username.setAlpha(1 - progress);
            binding.specialist.setAlpha(1 - progress);
            binding.moreInfo.setAlpha(1 - progress);
            binding.profileImage.setAlpha(1 - progress);
            binding.circle.setAlpha(1 - progress);

            // Показ/скрытие мини-аватарки
            binding.toolbar.setVisibility(progress > 0.7f ? View.VISIBLE : View.INVISIBLE);
            binding.profileImageSmall.setVisibility(progress > 0.7f ? View.VISIBLE : View.INVISIBLE);
            binding.username1.setVisibility(progress > 0.7f ? View.VISIBLE : View.INVISIBLE);
            binding.namesUser.setVisibility(progress > 0.7f ? View.VISIBLE : View.INVISIBLE);
            binding.specialist1.setVisibility(progress > 0.7f ? View.VISIBLE : View.INVISIBLE);
            binding.circleSmall.setVisibility(progress > 0.7f ? View.VISIBLE : View.INVISIBLE);
        });
    }

    private void animateViewsOnCreate() {
        //binding.profileImage.startAnimation(fadeInAnimation);

        /*
        binding.editProfileButton.postDelayed(() -> {
            binding.editProfileButton.startAnimation(fadeInAnimation);
        }, 150);*/


    }

    private void navigateToAccountSettings() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.menu_fr, new AccountSettingFragment()) // Заменяем текущий фрагмент
                .addToBackStack(null) // Добавляем в back stack для возможности возврата
                .commit();
    }


    //метод выгрузки информации о себе

    private <MutableLiveData> void loadFullName() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e(TAG, "Ошибка: пользователь не найден");
            return;
        }

        String userId = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app");

        //старый код
        /*// Получаем имя
        DatabaseReference databaseName = database.getReference("Users").child(userId).child("name");
        databaseName.get().addOnSuccessListener(nameBase -> {
            if (nameBase.exists() && nameBase.getValue() != null) {
                String name = nameBase.getValue(String.class);
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Ошибка загрузки имени: " + e.getMessage()));


        // Получаем фамилию
        DatabaseReference databaseSurname = database.getReference("Users").child(userId).child("surname");
        databaseSurname.get().addOnSuccessListener(surnameBase -> {
            if (surnameBase.exists() && surnameBase.getValue() != null) {
                String surname = surnameBase.getValue(String.class);
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Ошибка загрузки фамилии: " + e.getMessage()));

        // Получаем отчество
        DatabaseReference databaseDadsName = database.getReference("Users").child(userId).child("fathersName");
        databaseDadsName.get().addOnSuccessListener(dadsNameBase -> {
            if (dadsNameBase.exists() && dadsNameBase.getValue() != null) {
                String dadsName = dadsNameBase.getValue(String.class);
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Ошибка загрузки отчества: " + e.getMessage()));
        */

        Task<DataSnapshot> nameTask = database.getReference("Users").child(userId).child("name").get();
        Task<DataSnapshot> surnameTask = database.getReference("Users").child(userId).child("surname").get();
        Task<DataSnapshot> dadsNameTask = database.getReference("Users").child(userId).child("fathersName").get();

        Tasks.whenAllSuccess(nameTask, surnameTask, dadsNameTask)
                .addOnSuccessListener(results -> {
                    String name = ((DataSnapshot) results.get(0)).getValue(String.class);
                    String surname = ((DataSnapshot) results.get(1)).getValue(String.class);
                    String dadsName = ((DataSnapshot) results.get(2)).getValue(String.class);

                    String fullName = surname + " " + name + " " + dadsName;
                    String firstANDlast = surname + "\n" + name;
                    binding.fullName.setText(fullName);
                    binding.namesUser.setText(firstANDlast);

                    Log.d(TAG, "Полное имя: " + fullName);

                })
                .addOnFailureListener(e -> Log.e(TAG, "Ошибка загрузки: " + e.getMessage()));

    }

    private <MutableLiveData> void loadGenderANDdob() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e(TAG, "Ошибка: пользователь не найден");
            return;
        }

        String userId = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app");


        Task<DataSnapshot> genderTask = database.getReference("Users").child(userId).child("gender").get();
        Task<DataSnapshot> dateOfBirthTask = database.getReference("Users").child(userId).child("dateOfBirth").get();


        Tasks.whenAllSuccess(genderTask, dateOfBirthTask)
                .addOnSuccessListener(results -> {
                    String gender = ((DataSnapshot) results.get(0)).getValue(String.class);
                    String dateOfBirth = ((DataSnapshot) results.get(1)).getValue(String.class);

                    String GenderANDdob = gender + ", " + dateOfBirth;

                    binding.moreInfo.setText(GenderANDdob);

                    Log.d(TAG, "Полное имя: " + GenderANDdob);

                })
                .addOnFailureListener(e -> Log.e(TAG, "Ошибка загрузки: " + e.getMessage()));

    }

    private <MutableLiveData> void loadUserName() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e(TAG, "Ошибка: пользователь не найден");
            return;
        }

        String userId = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app");


        Task<DataSnapshot> unTask = database.getReference("Users").child(userId).child("username").get();

        Tasks.whenAllSuccess(unTask)
                .addOnSuccessListener(results -> {
                    String username = ((DataSnapshot) results.get(0)).getValue(String.class);

                    binding.username.setText(username);
                    binding.username1.setText(username);

                    Log.d(TAG, "Полное имя: " + username);

                })
                .addOnFailureListener(e -> Log.e(TAG, "Ошибка загрузки: " + e.getMessage()));

    }

    private <MutableLiveData> void loadUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e(TAG, "Ошибка: пользователь не найден");
            return;
        }

        String userId = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app");
        Task<DataSnapshot> unTask = database.getReference("Users").child(userId).child("aboutMyself").get();

        Tasks.whenAllSuccess(unTask)
                .addOnSuccessListener(results -> {
                    String info = ((DataSnapshot) results.get(0)).getValue(String.class);

                    binding.specialist.setText(info);

                    Log.d(TAG, "Информация о пользователе: " + info);

                })
                .addOnFailureListener(e -> Log.e(TAG, "Ошибка загрузки: " + e.getMessage()));
    }

    private void loadUserImageToProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app");
            DatabaseReference databaseReference = database.getReference("Users");

            String userId = user.getUid();
            databaseReference.child(userId).child("profileImageURL").get()
                    .addOnSuccessListener(dataSnapshot -> {
                        if (dataSnapshot.exists()) {
                            // Получаем ссылку на изображение
                            String imageUrl = dataSnapshot.getValue(String.class);
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Log.d(TAG, "Загружается изображение из Firebase: " + imageUrl);
                                // Загружаем изображение в ImageView с помощью Picasso
                                Picasso.get().load(imageUrl).into(binding.profileImage);
                                Picasso.get().load(imageUrl).into(binding.profileImageSmall);
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

    /// ТАБЫ
    private void setupViewPager() {
        binding.viewPager2.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                if (position == 0) {
                    return new UserExpertFragment();
                } else {
                    return new UserClientFragment();
                }
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });

        new TabLayoutMediator(binding.tabLayout, binding.viewPager2,
                (tab, position) -> {
                    tab.setText(position == 0 ? "Я эксперт" : "Я клиент");
                }
        ).attach();
    }

    private void openClientRequestForm() {
        // Создаем новый экземпляр фрагмента с формой запроса
        ClientRequestFragment requestFragment = new ClientRequestFragment();

        // Заменяем текущий фрагмент и добавляем в back stack
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.menu_fr, requestFragment) // Используем тот же контейнер, что и для настроек
                .addToBackStack("client_request") // Уникальное имя для back stack
                .commit();

        // Анимация перехода (опционально)
        if (fadeInAnimation != null) {
            addBtn.startAnimation(fadeInAnimation);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (scaleAnimation != null) scaleAnimation.cancel();
        if (fadeInAnimation != null) fadeInAnimation.cancel();
        binding = null;
    }

}
