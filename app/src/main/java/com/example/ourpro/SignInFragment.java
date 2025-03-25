package com.example.ourpro;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ourpro.databinding.FragmentSignInBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SignInFragment extends Fragment {

    private FragmentSignInBinding binding;

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

        binding.toSignup.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Переход к регистрации", Toast.LENGTH_SHORT).show();
            SignUpFragment signUpFragment = new SignUpFragment();
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.replace(R.id.sign_fr, signUpFragment);
            ft.commit();
        });

        binding.log.setOnClickListener(v -> {
            String email = binding.logInText.getText().toString().trim();
            String pass = binding.logPasswordText.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(requireContext(), "Поля не могут быть пустыми", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getActivity(), MainActivity.class));
                        } else {
                            Toast.makeText(requireContext(), "Ошибка: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}