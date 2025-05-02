package com.example.ourpro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logo);
        View layout = findViewById(R.id.splash_layout);

        // bounce-эффект логотипа
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
        logo.startAnimation(bounce);

        // спустя 2.5 сек — fade-out и переход на MainActivity
        new Handler().postDelayed(() -> {
            Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out_splash);
            layout.startAnimation(fadeOut);

            // Ждём окончания fadeOut, затем старт активности
            new Handler().postDelayed(() -> {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }, 600); // столько же, сколько fadeOut длится

        }, SPLASH_DURATION);
    }
}
