package com.example.ourpro.loginRegistration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.ourpro.MainActivity;
import com.example.ourpro.R;
import com.example.ourpro.databinding.FragmentSignUpBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
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

        binding.toSignin.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Вход в аккаунт", Toast.LENGTH_SHORT).show();
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.replace(R.id.sign_fr, new SignInFragment());
            ft.commit();
        });

        binding.log.setOnClickListener(v -> {
            String email = binding.mailInText.getText().toString();
            String username = binding.logUpText.getText().toString().trim();
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

            FirebaseDatabase database = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app/");
            DatabaseReference usersRef = database.getReference("Users");

            usersRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    boolean usernameExists = false;

                    for (DataSnapshot userSnapshot : task.getResult().getChildren()) {
                        String existingUsername = userSnapshot.child("username").getValue(String.class);
                        if (existingUsername != null && existingUsername.equalsIgnoreCase(username)) {
                            usernameExists = true;
                            break;
                        }
                    }

                    if (usernameExists) {
                        Toast.makeText(requireContext(), "Пользователь с таким именем уже существует", Toast.LENGTH_SHORT).show();
                    } else {
                        // Продолжить регистрацию
                        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(authTask -> {
                            if (authTask.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();

                                    HashMap<String, String> userInfo = new HashMap<>();
                                    userInfo.put("email", email);
                                    userInfo.put("username", username);
                                    userInfo.put("profileImageURL", "");
                                    userInfo.put("gender", "");
                                    userInfo.put("dateOfBirth", "");
                                    userInfo.put("surname", "");
                                    userInfo.put("name", "");
                                    userInfo.put("fathersName", "");
                                    userInfo.put("aboutMyself", "");
                                    userInfo.put("chats", "");
                                    userInfo.put("WiseCash", "");

                                    usersRef.child(userId).setValue(userInfo);

                                    startActivity(new Intent(requireActivity(), MainActivity.class));
                                }
                            } else {
                                Toast.makeText(requireContext(), "Ошибка регистрации: " + authTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } else {
                    Toast.makeText(requireContext(), "Ошибка при проверке имени: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
