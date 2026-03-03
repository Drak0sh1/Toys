package com.example.toyshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.toyshop.R;
import com.example.toyshop.database.DatabaseManager;
import com.example.toyshop.models.CartItem;
import com.example.toyshop.models.Toy;
import com.example.toyshop.utils.CartManager;

import java.text.NumberFormat;
import java.util.Locale;

public class ToyDetailActivity extends AppCompatActivity {

    private ImageView ivToyImage;
    private TextView tvToyName, tvToyPrice, tvToyCategory, tvToyAgeGroup,
            tvToyDescription, tvStockStatus, tvQuantity;
    private Button btnDecrease, btnIncrease, btnAddToCart, btnEdit, btnDelete;

    private String toyId;
    private String userId;
    private String userRole;
    private Toy currentToy;
    private int quantity = 1;

    private DatabaseManager databaseManager;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toy_detail);

        toyId = getIntent().getStringExtra("toy_id");
        userId = getIntent().getStringExtra("user_id");
        userRole = getIntent().getStringExtra("user_role");
        if (userRole == null) userRole = "user";

        databaseManager = DatabaseManager.getInstance(this);
        cartManager = CartManager.getInstance(this);

        initViews();
        setupToolbar();
        loadToyDetails();
        setupClickListeners();
    }

    private void initViews() {
        ivToyImage = findViewById(R.id.ivToyImage);
        tvToyName = findViewById(R.id.tvToyName);
        tvToyPrice = findViewById(R.id.tvToyPrice);
        tvToyCategory = findViewById(R.id.tvToyCategory);
        tvToyAgeGroup = findViewById(R.id.tvToyAgeGroup);
        tvToyDescription = findViewById(R.id.tvToyDescription);
        tvStockStatus = findViewById(R.id.tvStockStatus);
        tvQuantity = findViewById(R.id.tvQuantity);
        btnDecrease = findViewById(R.id.btnDecrease);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Детали товара");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
                            Toast.makeText(ToyDetailActivity.this, "Товар не найден", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        }).start();
    }

    private void displayToyDetails() {
        tvToyName.setText(currentToy.getName());

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("ru", "RU"));
        tvToyPrice.setText(format.format(currentToy.getPrice()));

        tvToyCategory.setText("Категория: " + currentToy.getCategory());
        tvToyAgeGroup.setText("Для детей " + currentToy.getAgeGroup() + "+");
        tvToyDescription.setText(currentToy.getDescription());

        tvQuantity.setText(String.valueOf(quantity));

        if (currentToy.isAvailable()) {
            tvStockStatus.setText("В наличии: " + currentToy.getStockQuantity() + " шт.");
            tvStockStatus.setTextColor(getColor(android.R.color.holo_green_dark));
            btnAddToCart.setEnabled(true);
        } else {
            tvStockStatus.setText("Нет в наличии");
            tvStockStatus.setTextColor(getColor(android.R.color.holo_red_dark));
            btnAddToCart.setEnabled(false);
        }

        if (currentToy.getImageUrl() != null && !currentToy.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(currentToy.getImageUrl())
                    .placeholder(R.drawable.placeholder_toy)
                    .into(ivToyImage);
        }

        // Проверяем роль пользователя
        if ("admin".equals(userRole)) {
            btnEdit.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
            btnAddToCart.setVisibility(View.GONE);
        } else {
            btnEdit.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
            btnAddToCart.setVisibility(View.VISIBLE);
        }
    }

    private void setupClickListeners() {
        btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 1) {
                    quantity--;
                    tvQuantity.setText(String.valueOf(quantity));
                }
            }
        });

        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentToy != null && quantity < currentToy.getStockQuantity()) {
                    quantity++;
                    tvQuantity.setText(String.valueOf(quantity));
                } else {
                    Toast.makeText(ToyDetailActivity.this,
                            "Недостаточно товара на складе", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ToyDetailActivity.this, AddEditToyActivity.class);
                intent.putExtra("toy_id", toyId);
                intent.putExtra("user_id", userId);
                startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteToy();
            }
        });
    }

    private void addToCart() {
        CartItem item = new CartItem(
                currentToy.getToyId(),
                currentToy.getName(),
                currentToy.getPrice(),
                quantity,
                currentToy.getImageUrl()
        );

        cartManager.addItem(item);
        Toast.makeText(this, "Товар добавлен в корзину", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void deleteToy() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = databaseManager.getDatabaseHelper().deleteToy(toyId);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (success) {
                            Toast.makeText(ToyDetailActivity.this,
                                    "Товар удален", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ToyDetailActivity.this,
                                    "Ошибка удаления", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }
}