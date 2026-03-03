package com.example.toyshop.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.toyshop.R;
import com.example.toyshop.database.DatabaseManager;
import com.example.toyshop.models.Toy;

import java.util.UUID;

public class AddEditToyActivity extends AppCompatActivity {

    private ImageView ivToyImage;
    private EditText etToyName, etDescription, etPrice, etStockQuantity;
    private Spinner spinnerCategory, spinnerAgeGroup;
    private Button btnSelectImage, btnSave, btnCancel;

    private String toyId;
    private String userId;
    private Toy currentToy;
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
                                .placeholder(R.drawable.placeholder_toy)
                                .into(ivToyImage);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_toy);

        toyId = getIntent().getStringExtra("toy_id");
        userId = getIntent().getStringExtra("user_id");
        databaseManager = DatabaseManager.getInstance(this);

        initViews();
        setupToolbar();
        setupSpinners();

        if (toyId != null) {
            loadToyDetails();
        }

        setupClickListeners();
    }

    private void initViews() {
        ivToyImage = findViewById(R.id.ivToyImage);
        etToyName = findViewById(R.id.etToyName);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        etStockQuantity = findViewById(R.id.etStockQuantity);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerAgeGroup = findViewById(R.id.spinnerAgeGroup);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(toyId == null ? "Добавить товар" : "Редактировать товар");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupSpinners() {
        // Категории
        String[] categories = {"Конструкторы", "Мягкие игрушки", "Машинки",
                "Куклы", "Развивающие", "Настольные игры", "Другое"};
        android.widget.ArrayAdapter<String> categoryAdapter = new android.widget.ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Возрастные группы
        String[] ageGroups = {"0+", "1+", "2+", "3+", "4+", "5+", "6+", "7+", "8+", "9+", "10+", "12+", "14+", "16+", "18+"};
        android.widget.ArrayAdapter<String> ageAdapter = new android.widget.ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, ageGroups);
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAgeGroup.setAdapter(ageAdapter);
    }

    private void loadToyDetails() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                currentToy = databaseManager.getDatabaseHelper().getToyById(toyId);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (currentToy != null) {
                            displayToyDetails();
                        } else {
                            Toast.makeText(AddEditToyActivity.this,
                                    "Товар не найден", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        }).start();
    }

    private void displayToyDetails() {
        etToyName.setText(currentToy.getName());
        etDescription.setText(currentToy.getDescription());
        etPrice.setText(String.valueOf(currentToy.getPrice()));
        etStockQuantity.setText(String.valueOf(currentToy.getStockQuantity()));

        // Устанавливаем категорию
        String[] categories = getResources().getStringArray(R.array.categories_array);
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(currentToy.getCategory())) {
                spinnerCategory.setSelection(i);
                break;
            }
        }

        // Устанавливаем возрастную группу
        String[] ageGroups = getResources().getStringArray(R.array.age_groups_array);
        String ageStr = currentToy.getAgeGroup() + "+";
        for (int i = 0; i < ageGroups.length; i++) {
            if (ageGroups[i].equals(ageStr)) {
                spinnerAgeGroup.setSelection(i);
                break;
            }
        }

        if (currentToy.getImageUrl() != null && !currentToy.getImageUrl().isEmpty()) {
            selectedImageUri = currentToy.getImageUrl();
            Glide.with(AddEditToyActivity.this)
                    .load(currentToy.getImageUrl())
                    .placeholder(R.drawable.placeholder_toy)
                    .into(ivToyImage);
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
                saveToy();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void saveToy() {
        String name = etToyName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String stockStr = etStockQuantity.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        String ageGroupStr = spinnerAgeGroup.getSelectedItem().toString();

        if (name.isEmpty()) {
            etToyName.setError("Введите название");
            return;
        }

        if (description.isEmpty()) {
            etDescription.setError("Введите описание");
            return;
        }

        if (priceStr.isEmpty()) {
            etPrice.setError("Введите цену");
            return;
        }

        if (stockStr.isEmpty()) {
            etStockQuantity.setError("Введите количество");
            return;
        }

        double price;
        int stock;
        int ageGroup;

        try {
            price = Double.parseDouble(priceStr);
            stock = Integer.parseInt(stockStr);
            ageGroup = Integer.parseInt(ageGroupStr.replace("+", ""));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Проверьте правильность введенных данных", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSave.setEnabled(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success;

                if (toyId == null) {
                    // Добавление нового товара
                    Toy newToy = new Toy(
                            UUID.randomUUID().toString(),
                            name,
                            description,
                            price,
                            category,
                            ageGroup,
                            stock
                    );
                    newToy.setImageUrl(selectedImageUri);
                    success = databaseManager.getDatabaseHelper().addToy(newToy);
                } else {
                    // Обновление существующего товара
                    currentToy.setName(name);
                    currentToy.setDescription(description);
                    currentToy.setPrice(price);
                    currentToy.setCategory(category);
                    currentToy.setAgeGroup(ageGroup);
                    currentToy.setStockQuantity(stock);
                    currentToy.setImageUrl(selectedImageUri);
                    success = databaseManager.getDatabaseHelper().updateToy(currentToy);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnSave.setEnabled(true);

                        if (success) {
                            Toast.makeText(AddEditToyActivity.this,
                                    toyId == null ? "Товар добавлен" : "Товар обновлен",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddEditToyActivity.this,
                                    "Ошибка сохранения", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }
}