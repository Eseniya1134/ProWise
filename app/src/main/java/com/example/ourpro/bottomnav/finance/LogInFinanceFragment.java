package com.example.ourpro.bottomnav.finance;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.ourpro.databinding.FragmentLogInFinanceBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class LogInFinanceFragment extends Fragment {
    private ViewFlipper viewFlipper;
    private FragmentLogInFinanceBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentLogInFinanceBinding.inflate(inflater, container, false);
        View view = binding.getRoot();



        binding.btnCreate.setOnClickListener(v -> {
            if (binding.viewFlipper.getDisplayedChild() < 1) {
                binding.viewFlipper.showNext();
            } else {
                Toast.makeText(getContext(), "Онбординг завершен!", Toast.LENGTH_SHORT).show();
                // переход на главный экран или сохранение состояния
            }
        });

        binding.btnCheck.setOnClickListener(v -> {
            flagInFbdb();
        });

        binding.btnSave.setOnClickListener(v -> {
            if (binding.viewFlipper.getDisplayedChild() == 2) {
                binding.viewFlipper.showNext();
            }
        });

        binding.btnCheckPin.setOnClickListener(v -> {
            if (binding.viewFlipper.getDisplayedChild() == 3) {
                binding.viewFlipper.showNext();
            }
        });

        binding.btnNext.setOnClickListener(v -> {
            if (binding.viewFlipper.getDisplayedChild() == 4) {
                binding.viewFlipper.showNext();
            }
        });



        return view;
    }

    ///2 ШАГ
    //Ставит флажок о наличии WiseCash-а в личном кабинете firebase
    private void flagInFbdb() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app");
            DatabaseReference databaseReference = database.getReference("Users");

            String userId = user.getUid();

            DatabaseReference databaseUserName = databaseReference.child(userId).child("username");
            databaseUserName.get().addOnSuccessListener(UserNameBase -> {
                if (UserNameBase.exists() && UserNameBase.getValue() != null) {
                    String presentUsername = UserNameBase.getValue(String.class);
                    if (presentUsername != null) {
                        // Нормализация строк: обрезка пробелов и приведение к нижнему регистру
                        String username = binding.usernameGet.getText().toString();
                        String trimmedInput = username.trim().toLowerCase();
                        String trimmedPresent = presentUsername.trim().toLowerCase();
                        Log.d(TAG, trimmedPresent + trimmedInput);

                        Map<String, Object> userUpdates = new HashMap<>();
                        if (!trimmedInput.isEmpty() && trimmedInput.equals(trimmedPresent)) {
                            userUpdates.put("WiseCash", "true");
                        } else {
                            Toast.makeText(requireContext(), "Имя пользователя введено неверно!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        databaseReference.child(userId).updateChildren(userUpdates)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Кошелек создан в Firebase");
                                    if (binding.viewFlipper.getDisplayedChild() == 1) {
                                        binding.viewFlipper.showNext();
                                    }
                                })
                                .addOnFailureListener(e -> Log.e(TAG, "Ошибка: " + e.getMessage()));
                    }
                } else {
                    Toast.makeText(requireContext(), "Имя пользователя не найдено в базе!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(requireContext(), "Ошибка загрузки данных: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    ///4 ШАГ
    //Введение ПИНА
    private void PinGet(){


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}