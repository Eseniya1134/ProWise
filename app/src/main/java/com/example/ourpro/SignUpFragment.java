package com.example.ourpro;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.example.ourpro.databinding.FragmentSignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpFragment extends Fragment {

    private FragmentSignUpBinding binding;
    private FirebaseAuth auth;

    public SignUpFragment() {
        super(R.layout.fragment_sign_up);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentSignUpBinding.bind(view);
        auth = FirebaseAuth.getInstance();

        //   FirebaseDatabase.getInstance().setPersistenceEnabled(true);
       // database = FirebaseDatabase.getInstance().getReference().child("Users");


        binding.toSignin.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Вход в аккаунт", Toast.LENGTH_SHORT).show();
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.replace(R.id.sign_fr, new SignInFragment());
            ft.commit();
        });

        binding.log.setOnClickListener(v -> {
            String email = binding.mailInText.getText().toString();
            String username = binding.logUpText.getText().toString();
            String password = binding.logPasswordText.getText().toString();
            String confirmPassword = binding.logPasswordRepText.getText().toString();

            if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(requireContext(), "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                Log.d("FirebaseAuth", "Началась регистрация...");

                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();

                    if (user != null) {
                        String userId = user.getUid();
                        Log.d("FirebaseAuth", "Пользователь зарегистрирован: " + userId);

                        HashMap<String, String> userInfo = new HashMap<>();
                        userInfo.put("email", email);
                        userInfo.put("username", username);
                        userInfo.put("profileImage", "");
                        userInfo.put("chats", "");

                        Log.d("FirebaseDatabase", "Пытаюсь сохранить данные пользователя...");

                        Log.d("FirebaseDatabase", "UID пользователя: " + auth.getCurrentUser().getUid());

                        // Укажите правильный URL базы данных
                        FirebaseDatabase database = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app");
                        DatabaseReference ref = database.getReference().child("Users");
                        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(userInfo);

                        startActivity(new Intent(requireActivity(), MainActivity.class));

                    } else {
                        Log.e("FirebaseAuth", "Ошибка: `getCurrentUser()` вернул null");
                        Toast.makeText(requireContext(), "Ошибка: пользователь не найден", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("FirebaseAuth", "Ошибка регистрации: " + task.getException().getMessage());
                    Toast.makeText(requireContext(), "Ошибка регистрации: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

}
