package com.example.toyshop.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.toyshop.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000; // 2 секунды

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUserSession();
            }
        }, SPLASH_DURATION);
    }

    private void checkUserSession() {
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);
        String userRole = prefs.getString("user_role", null);

        Intent intent;
        if (userId != null && userRole != null) {
            // Пользователь уже вошел
            if ("admin".equals(userRole)) {
                intent = new Intent(SplashActivity.this, AdminMainActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, UserMainActivity.class);
            }
            intent.putExtra("user_id", userId);
            intent.putExtra("user_role", userRole);
        } else {
            // Пользователь не вошел
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }
}