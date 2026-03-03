package com.example.toyshop.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.toyshop.R;
import com.example.toyshop.database.DatabaseManager;
import com.example.toyshop.models.User;
import com.example.toyshop.utils.CartManager;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView ivProfileImage;
    private TextView tvEmail;
    private EditText etFullName, etPhone;
    private Button btnSave, btnChangePassword, btnLogout, btnSelectImage;

    private String userId;
    private String userRole;
    private User currentUser;
    private String selectedImageUri = "";

    private DatabaseManager databaseManager;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        selectedImageUri = imageUri.toString();
                        Glide.with(this)
                                .load(imageUri)
                                .placeholder(R.drawable.ic_profile)
                                .into(ivProfileImage);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userId = getIntent().getStringExtra("user_id");
        if (userId == null) {
            SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
            userId = prefs.getString("user_id", null);
            userRole = prefs.getString("user_role", null);
        }

        databaseManager = DatabaseManager.getInstance(this);

        initViews();
        setupToolbar();
        loadUserProfile();
        setupClickListeners();
    }

    private void initViews() {
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvEmail = findViewById(R.id.tvEmail);
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        btnSave = findViewById(R.id.btnSave);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);
        btnSelectImage = findViewById(R.id.btnSelectImage);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Мой профиль");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadUserProfile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                currentUser = databaseManager.getDatabaseHelper().getUserById(userId);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (currentUser != null) {
                            displayUserProfile();
                        } else {
                            Toast.makeText(ProfileActivity.this,
                                    "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        }).start();
    }

    private void displayUserProfile() {
        tvEmail.setText(currentUser.getEmail());
        etFullName.setText(currentUser.getFullName());
        etPhone.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "");

        if (currentUser.getProfileImageUrl() != null && !currentUser.getProfileImageUrl().isEmpty()) {
            selectedImageUri = currentUser.getProfileImageUrl();
            Glide.with(this)
                    .load(currentUser.getProfileImageUrl())
                    .placeholder(R.drawable.ic_profile)
                    .into(ivProfileImage);
        }
    }

    private void setupClickListeners() {
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void saveProfile() {
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (fullName.isEmpty()) {
            etFullName.setError("Введите имя");
            return;
        }

        btnSave.setEnabled(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                currentUser.setFullName(fullName);
                currentUser.setPhone(phone);
                currentUser.setProfileImageUrl(selectedImageUri);

                boolean success = databaseManager.getDatabaseHelper().updateUser(currentUser);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnSave.setEnabled(true);

                        if (success) {
                            Toast.makeText(ProfileActivity.this,
                                    "Профиль обновлен", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfileActivity.this,
                                    "Ошибка обновления", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        builder.setView(dialogView);

        EditText etOldPassword = dialogView.findViewById(R.id.etOldPassword);
        EditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);
        EditText etConfirmPassword = dialogView.findViewById(R.id.etConfirmPassword);

        builder.setTitle("Изменение пароля")
                .setPositiveButton("Изменить", (dialog, which) -> {
                    String oldPassword = etOldPassword.getText().toString();
                    String newPassword = etNewPassword.getText().toString();
                    String confirmPassword = etConfirmPassword.getText().toString();

                    if (newPassword.length() < 6) {
                        Toast.makeText(this, "Пароль должен быть не менее 6 символов", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!newPassword.equals(confirmPassword)) {
                        Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // В реальном проекте здесь нужно проверить старый пароль
                    Toast.makeText(this, "Функция изменения пароля будет добавлена", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void logout() {
        // Очищаем сессию
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        prefs.edit().clear().apply();

        // Очищаем корзину
        CartManager.getInstance(this).clearCart();

        // Переходим на экран входа
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}