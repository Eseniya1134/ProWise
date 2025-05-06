// LogInFinanceFragment.java
package com.example.ourpro.bottomnav.finance;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ourpro.R;
import com.example.ourpro.bottomnav.dialogs.DialogsFragment;
import com.example.ourpro.databinding.FragmentLogInFinanceBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class LogInFinanceFragment extends Fragment {

    private FragmentLogInFinanceBinding binding;
    private ImageView[] pinCircles = new ImageView[4];
    private String pin;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLogInFinanceBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Переход от 0 → 1
        binding.btnCreate.setOnClickListener(v -> {
            if (binding.viewFlipper.getDisplayedChild() == 0) {
                binding.viewFlipper.showNext();
            }
        });

        // Проверка имени и переход 1 → 2 → 3
        binding.btnCheck.setOnClickListener(v -> flagInFbdb());

        // Переход на финальный экран (если вдруг потребуется)
        binding.btnNext.setOnClickListener(v -> {
            if (binding.viewFlipper.getDisplayedChild() == 4) {
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.replace(R.id.menu_fr, new FinanceFragment());
                ft.addToBackStack(null); // Добавляем в back stack
                ft.commit();
            }
        });

        return view;
    }

    /// ШАГ 2 — Проверка имени
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
                    String inputUsername = binding.usernameGet.getText().toString().trim().toLowerCase();

                    if (presentUsername != null && inputUsername.equals(presentUsername.trim().toLowerCase())) {
                        Map<String, Object> userUpdates = new HashMap<>();
                        userUpdates.put("WiseCash", "true");

                        databaseReference.child(userId).updateChildren(userUpdates).addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Кошелек создан в Firebase");

                            if (binding.viewFlipper.getDisplayedChild() == 1) {
                                binding.viewFlipper.showNext();

                                // Ждём перехода к экрану и инициализируем PIN
                                new Handler(Looper.getMainLooper()).postDelayed(this::initPinInput, 200);
                            }
                        }).addOnFailureListener(e -> Log.e(TAG, "Ошибка: " + e.getMessage()));
                    } else {
                        Toast.makeText(requireContext(), "Имя пользователя введено неверно!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Имя пользователя не найдено в базе!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(requireContext(), "Ошибка загрузки данных: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    /// ШАГ 3 — Ввод PIN-кода
    private void initPinInput() {
        pinCircles[0] = binding.pinCircle1;
        pinCircles[1] = binding.pinCircle2;
        pinCircles[2] = binding.pinCircle3;
        pinCircles[3] = binding.pinCircle4;

        binding.pinEditText.setVisibility(View.VISIBLE);
        binding.pinEditText.setCursorVisible(false);
        binding.pinEditText.requestFocus();

        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(binding.pinEditText, InputMethodManager.SHOW_IMPLICIT);
        }

        binding.pinEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePinCircles(s.length());
            }
        });

        binding.btnSave.setOnClickListener(v -> {
            String pinCode = binding.pinEditText.getText().toString();
            if (pinCode.length() == 4) {
                pin = pinCode;
                Log.d("PIN_INPUT", "Введённый PIN: " + pinCode);
                Toast.makeText(getContext(), "Введённый PIN: " + pinCode, Toast.LENGTH_SHORT).show();

                if (binding.viewFlipper.getDisplayedChild() == 2) {
                    binding.viewFlipper.showNext();
                    new Handler(Looper.getMainLooper()).postDelayed(this::initPinInput2, 200);
                }
            } else {
                Toast.makeText(requireContext(), "Введите 4 цифры", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /// ШАГ 4 — Подтверждение PIN-кода
    private void initPinInput2() {
        pinCircles[0] = binding.pin2Circle1;
        pinCircles[1] = binding.pin2Circle2;
        pinCircles[2] = binding.pin2Circle3;
        pinCircles[3] = binding.pin2Circle4;

        binding.pinEditText2.setVisibility(View.VISIBLE);
        binding.pinEditText2.setCursorVisible(false);
        binding.pinEditText2.requestFocus();

        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(binding.pinEditText2, InputMethodManager.SHOW_IMPLICIT);
        }

        binding.pinEditText2.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePinCircles(s.length());
            }
        });

        binding.btnCheckPin.setOnClickListener(v -> {
            String pinCode = binding.pinEditText2.getText().toString();
            if (pinCode.length() == 4) {
                if (pinCode.equals(pin)) {
                    Toast.makeText(getContext(), "PIN совпадает", Toast.LENGTH_SHORT).show();
                    if (binding.viewFlipper.getDisplayedChild() == 3) {
                        binding.viewFlipper.showNext();
                    }
                } else {
                    Toast.makeText(requireContext(), "PIN-код не совпадает", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Введите 4 цифры", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /// Обновление кружочков
    private void updatePinCircles(int length) {
        for (int i = 0; i < 4; i++) {
            if (i < length) {
                pinCircles[i].setImageResource(R.drawable.pin_circle_filled);
            } else {
                pinCircles[i].setImageResource(R.drawable.pin_circle_empty);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
