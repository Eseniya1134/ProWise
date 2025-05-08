package com.example.ourpro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000;
    private static final int TEXT_ANIMATION_DELAY = 800; // Задержка перед анимацией текста

    private void animateTypingText(TextView textView, String fullText) {
        textView.setText("");
        for (int i = 0; i < fullText.length(); i++) {
            final int index = i;
            new Handler().postDelayed(() -> {
                String currentText = textView.getText().toString();
                textView.setText(currentText + fullText.charAt(index));
            }, i * 80L);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logo);
        TextView welcomeText = findViewById(R.id.welcome);
        View layout = findViewById(R.id.splash_layout);

        // Сначала скрываем текст
        welcomeText.setVisibility(View.INVISIBLE);

        // bounce-эффект логотипа
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
        logo.startAnimation(bounce);

        // Запускаем анимацию текста с задержкой
        new Handler().postDelayed(() -> {
            welcomeText.setVisibility(View.VISIBLE);

            animateTypingText(welcomeText, "Добро пожаловать!");

        }, TEXT_ANIMATION_DELAY);



        // спустя 2.5 сек — fade-out и переход на MainActivity
        new Handler().postDelayed(() -> {
            Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out_splash);
            layout.startAnimation(fadeOut);

            new Handler().postDelayed(() -> {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }, 600);

        }, SPLASH_DURATION);
    }
}