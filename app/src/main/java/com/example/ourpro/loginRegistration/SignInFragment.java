package com.example.ourpro.loginRegistration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ourpro.MainActivity;
import com.example.ourpro.R;
import com.example.ourpro.databinding.FragmentSignInBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignInFragment extends Fragment {

    private FragmentSignInBinding binding;
    private FirebaseAuth auth;
    private DatabaseReference usersRef;
    private static final String TAG = "SignIn";

    public SignInFragment() {
        super(R.layout.fragment_sign_in);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("Users");

        binding.toSignup.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Переход к регистрации", Toast.LENGTH_SHORT).show();
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.replace(R.id.sign_fr, new SignUpFragment());
            ft.commit();
        });

        binding.log.setOnClickListener(v -> {
            String login = binding.logInText.getText().toString().trim();
            String pass = binding.logPasswordText.getText().toString().trim();

            if (login.isEmpty() || pass.isEmpty()) {
                Toast.makeText(requireContext(), "Поля не могут быть пустыми", Toast.LENGTH_SHORT).show();
                return;
            }

            if (login.contains("@")) {
                // Вход по почте
                signInWithEmail(login, pass);
            } else {
                // Вход по нику
                signInWithUsername(login, pass);
            }
        });
    }

    private void signInWithEmail(String email, String pass) {
        auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        requireActivity().finish();
                    } else {
                        Toast.makeText(requireContext(), "Ошибка входа: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signInWithUsername(String username, String password) {
        Query query = usersRef.orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String email = userSnapshot.child("email").getValue(String.class);
                        if (email != null) {
                            signInWithEmail(email, password);
                        } else {
                            Toast.makeText(requireContext(), "Email не найден для пользователя", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }
                } else {
                    Toast.makeText(requireContext(), "Пользователь с таким ником не найден", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Ошибка: пользователь не найден");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Ошибка базы данных: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        });
    }
}
