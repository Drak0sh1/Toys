package com.example.toyshop.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.toyshop.R;
import com.example.toyshop.database.DatabaseManager;
import com.example.toyshop.models.User;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private ProgressBar progressBar;

    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseManager = DatabaseManager.getInstance(this);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void loginUser() {
        if (etEmail == null || etPassword == null) return;
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Введите email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Введите пароль");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        // Проверяем в отдельном потоке, чтобы не блокировать UI
        new Thread(new Runnable() {
            @Override
            public void run() {
                User user = null;
                try {
                    user = databaseManager.getDatabaseHelper().loginUser(email, password);
                } catch (Exception e) {
                    android.util.Log.e("LoginActivity", "Login error", e);
                }

                final User finalUser = user;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isFinishing() || isDestroyed()) return;

                        progressBar.setVisibility(View.GONE);
                        btnLogin.setEnabled(true);

                        if (finalUser != null) {
                            try {
                                SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
                                String role = finalUser.getRole() != null ? finalUser.getRole() : "user";
                                prefs.edit()
                                        .putString("user_id", finalUser.getUserId())
                                        .putString("user_role", role)
                                        .apply();

                                Intent intent;
                                if ("admin".equals(role)) {
                                    intent = new Intent(LoginActivity.this, AdminMainActivity.class);
                                } else {
                                    intent = new Intent(LoginActivity.this, UserMainActivity.class);
                                }

                                intent.putExtra("user_id", finalUser.getUserId());
                                intent.putExtra("user_role", role);

                                startActivity(intent);
                                finish();
                            } catch (Exception e) {
                                android.util.Log.e("LoginActivity", "Start activity error", e);
                                Toast.makeText(LoginActivity.this,
                                        "Ошибка входа: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this,
                                    "Неверный email или пароль",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }
}