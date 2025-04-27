package com.example.ourpro.user;

import static com.example.ourpro.utils.ChatUtil.generateChatId;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ourpro.R;
import com.example.ourpro.bottomnav.dialogs.ChatsFragment;
import com.example.ourpro.bottomnav.profile.AccountSettingFragment;
import com.example.ourpro.databinding.FragmentUserProfileBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserProfileFragment extends Fragment {

    public UserProfileFragment() {
        super(R.layout.fragment_user_profile);
    }

    private static final String TAG = "Upload ###";

    private FragmentUserProfileBinding binding;
    private Animation scaleAnimation, fadeInAnimation;

    String userId;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentUserProfileBinding.bind(view);

        loadFullName();
        loadGenderANDdob();
        loadUserName();

        //binding.exit.setOnClickListener(v -> logout());
        //binding.exit2.setOnClickListener(v -> logout());

        // Инициализация анимаций
        scaleAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale);
        fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in);


        binding.buttonWrite.setOnClickListener(v -> {
            createNewChat();
            String chatId = myUserId();
            String otherUserId = getOtherUserId();
            openChat(chatId, otherUserId);

        });

        // Настройка поведения AppBar
        setupAppBar();

        // Анимация при открытии
        animateViewsOnCreate();

    }

    private void createNewChat() {


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Ошибка: пользователь не авторизован", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserId = currentUser.getUid();
        String otherUserId = getOtherUserId(); // Тебе нужно знать с кем создаешь чат

        if (otherUserId == null) {
            Toast.makeText(getContext(), "Ошибка: выберите пользователя для чата", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference db= FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("Chats");

        String chatId = generateChatId(currentUserId, otherUserId);

        db.child("Chats").child(chatId).child("Users").child(currentUserId).setValue(true);
        db.child("Chats").child(chatId).child("Users").child(otherUserId).setValue(true);

// + Новое: добавляем чат пользователям
        db.child("Users").child(currentUserId).child("chats").get().addOnSuccessListener(snapshot -> {
            String chats = snapshot.getValue(String.class);
            if (chats == null || chats.isEmpty()) {
                chats = chatId;
            } else {
                chats += "," + chatId;
            }
            db.child("Users").child(currentUserId).child("chats").setValue(chats);
        });

        db.child("Users").child(otherUserId).child("chats").get().addOnSuccessListener(snapshot -> {
            String chats = snapshot.getValue(String.class);
            if (chats == null || chats.isEmpty()) {
                chats = chatId;
            } else {
                chats += "," + chatId;
            }
            db.child("Users").child(otherUserId).child("chats").setValue(chats);
        });

        Toast.makeText(getContext(), "Чат создан!", Toast.LENGTH_SHORT).show();

        // Здесь можно открыть экран самого чата (если хочешь)
        // openChat(chatId);
    }

    private String generateChatId(String userId1, String userId2) {
        if (userId1.compareTo(userId2) < 0) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

    private void openChat(String chatId, String otherUserId) {
        Bundle bundle = new Bundle();
        bundle.putString("chatId", chatId);
        bundle.putString("otherUserId", otherUserId);

        ChatsFragment chatFragment = new ChatsFragment();
        chatFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.menu_fr, chatFragment)
                .addToBackStack(null)
                .commit();
    }

    private String myUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e(TAG, "Ошибка: пользователь не найден");
        }

        String userId = user.getUid();
        return userId;
    }

    private String getOtherUserId() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app");


        // Получаем userId из аргументов
        userId = getArguments() != null ? getArguments().getString("userId") : null;
        if (userId == null) {
            Toast.makeText(getContext(), "Пользователь не найден", Toast.LENGTH_SHORT).show();

        }
        return userId; // сюда временно вручную вставь ID любого пользователя
    }
    private void setupAppBar() {
        binding.appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int scrollRange = appBarLayout.getTotalScrollRange();
            float progress = -verticalOffset / (float) scrollRange;

            // Анимация прозрачности элементов
            //binding.exit.setAlpha(1 - progress);
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


    //метод выгрузки фио и "О себе"

    private <MutableLiveData> void loadFullName() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app");


        // Получаем userId из аргументов
        userId = getArguments() != null ? getArguments().getString("userId") : null;
        if (userId == null) {
            Toast.makeText(getContext(), "Пользователь не найден", Toast.LENGTH_SHORT).show();
            return;
        }


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

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app");


        // Получаем userId из аргументов
        userId = getArguments() != null ? getArguments().getString("userId") : null;
        if (userId == null) {
            Toast.makeText(getContext(), "Пользователь не найден", Toast.LENGTH_SHORT).show();
            return;
        }



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

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app");


        // Получаем userId из аргументов
        userId = getArguments() != null ? getArguments().getString("userId") : null;
        if (userId == null) {
            Toast.makeText(requireContext(), "Пользователь не найден", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("UserProfileFragment", "Получен userId: " + userId);




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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (scaleAnimation != null) scaleAnimation.cancel();
        if (fadeInAnimation != null) fadeInAnimation.cancel();
        binding = null;
    }

}
