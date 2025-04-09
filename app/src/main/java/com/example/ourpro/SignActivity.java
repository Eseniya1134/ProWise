package com.example.ourpro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignActivity extends AppCompatActivity {

    private FrameLayout frameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        frameLayout = findViewById(R.id.sign_fr);

        // Загружаем фрагмент SignInFragment
        SignInFragment signInFragment = new SignInFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.sign_fr, signInFragment);
        ft.commit();

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            // Уже вошёл — направить в основное активити
            startActivity(new Intent(this, MainActivity.class)); // или BottomNavActivity
            finish();
        } else {
            // Не вошёл — оставить на SignActivity
        }
    }
}
