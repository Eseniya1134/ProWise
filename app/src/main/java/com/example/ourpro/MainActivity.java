package com.example.ourpro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ourpro.bottomnav.catalog.CatalogFragment;
import com.example.ourpro.bottomnav.finance.FinanceFragment;
import com.example.ourpro.bottomnav.finance.LogInFinanceFragment;
import com.example.ourpro.bottomnav.orders.OrdersFragment;

import com.example.ourpro.bottomnav.dialogs.DialogsFragment;
import com.example.ourpro.bottomnav.profile.AccountSettingFragment;
import com.example.ourpro.bottomnav.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import com.example.ourpro.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, SignActivity.class));
            finish();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app");

        DatabaseReference databaseName = database.getReference("Users").child(userId).child("name");
        databaseName.get().addOnSuccessListener(snapshot -> {
            String name = snapshot.getValue(String.class);

            if (name == null || name.trim().isEmpty()) {
                getSupportFragmentManager().beginTransaction().replace(binding.menuFr.getId(), new AccountSettingFragment()).commit();
                binding.bottomNavigation.setSelectedItemId(R.id.profile);
                Map<Integer, Fragment> fragmentMap = new HashMap<>();
                fragmentMap.put(R.id.catalog, new CatalogFragment());
                fragmentMap.put(R.id.finance, new LogInFinanceFragment());
                fragmentMap.put(R.id.orders, new OrdersFragment());
                fragmentMap.put(R.id.profile, new AccountSettingFragment());
                fragmentMap.put(R.id.dialogs, new DialogsFragment());

                binding.bottomNavigation.setOnItemSelectedListener(item -> {
                    Fragment fragment = fragmentMap.get(item.getItemId());
                    getSupportFragmentManager().beginTransaction().replace(binding.menuFr.getId(), fragment).commit();
                    return true;
                });
            } else {
                // Перекидываем после входа автоматически на вкладку профиль
                getSupportFragmentManager().beginTransaction().replace(binding.menuFr.getId(), new ProfileFragment()).commit();
                binding.bottomNavigation.setSelectedItemId(R.id.profile);
                Map<Integer, Fragment> fragmentMap = new HashMap<>();
                fragmentMap.put(R.id.catalog, new CatalogFragment());
                fragmentMap.put(R.id.finance, new LogInFinanceFragment());
                fragmentMap.put(R.id.orders, new OrdersFragment());
                fragmentMap.put(R.id.profile, new ProfileFragment());
                fragmentMap.put(R.id.dialogs, new DialogsFragment());

                binding.bottomNavigation.setOnItemSelectedListener(item -> {
                    Fragment fragment = fragmentMap.get(item.getItemId());

                    getSupportFragmentManager().beginTransaction().replace(binding.menuFr.getId(), fragment).commit();

                    return true;
                });
            }
        });
    }


    private void check(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String userId = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://prowise-de1d0-default-rtdb.europe-west1.firebasedatabase.app");

        DatabaseReference databaseName = database.getReference("Users").child(userId).child("name");

        databaseName.get().addOnSuccessListener(nameBase -> {
            Object value = nameBase.getValue();

            if (value == null || value.toString().trim().isEmpty()) {
                // Имя не указано — отправляем на настройки
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(binding.menuFr.getId(), new AccountSettingFragment())
                        .commit();

                binding.bottomNavigation.setSelectedItemId(R.id.profile); // обновим вкладку
            }
        }).addOnFailureListener(e -> {
            Log.e("MainActivity", "Ошибка получения имени: " + e.getMessage());
        });
    }


}
