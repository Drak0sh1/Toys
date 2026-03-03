package com.example.toyshop.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toyshop.R;
import com.example.toyshop.adapters.ToyAdapter;
import com.example.toyshop.database.DatabaseManager;
import com.example.toyshop.models.CartItem;
import com.example.toyshop.models.Toy;
import com.example.toyshop.utils.CartManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class UserMainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ToyAdapter toyAdapter;
    private List<Toy> toyList = new ArrayList<>();
    private BottomNavigationView bottomNavigation;
    private TextView tvCartBadge;
    private FloatingActionButton fabCart;
    private String userId;
    private String userRole;

    private DatabaseManager databaseManager;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        userId = getIntent().getStringExtra("user_id");
        userRole = getIntent().getStringExtra("user_role");
        if (userRole == null) userRole = "user";

        try {
            databaseManager = DatabaseManager.getInstance(this);
            cartManager = CartManager.getInstance(this);
        } catch (Exception e) {
            android.util.Log.e("UserMainActivity", "Init error", e);
            Toast.makeText(this, "Ошибка инициализации", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadToys();
        setupBottomNavigation();
        updateCartBadge();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        fabCart = findViewById(R.id.fabCart);
        tvCartBadge = findViewById(R.id.tvCartBadge);

        if (fabCart == null || recyclerView == null || bottomNavigation == null || tvCartBadge == null) {
            Toast.makeText(this, "Ошибка загрузки интерфейса", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fabCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserMainActivity.this, CartActivity.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Магазин игрушек");
            }
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        toyAdapter = new ToyAdapter(this, toyList, false);

        toyAdapter.setOnItemClickListener(new ToyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Toy toy) {
                Intent intent = new Intent(UserMainActivity.this, ToyDetailActivity.class);
                intent.putExtra("toy_id", toy.getToyId());
                intent.putExtra("user_id", userId);
                intent.putExtra("user_role", userRole);
                startActivity(intent);
            }

            @Override
            public void onEditClick(Toy toy) {
                // Не используется для пользователя
            }

            @Override
            public void onDeleteClick(Toy toy) {
                // Не используется для пользователя
            }

            @Override
            public void onAddToCartClick(Toy toy) {
                addToCart(toy);
            }
        });

        recyclerView.setAdapter(toyAdapter);
    }

    private void loadToys() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Toy> toys = databaseManager.getDatabaseHelper().getAllToys();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toyList.clear();
                        toyList.addAll(toys);
                        toyAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void addToCart(Toy toy) {
        CartItem item = new CartItem(
                toy.getToyId(),
                toy.getName(),
                toy.getPrice(),
                1,
                toy.getImageUrl()
        );

        cartManager.addItem(item);
        updateCartBadge();

        Toast.makeText(this, "Товар добавлен в корзину", Toast.LENGTH_SHORT).show();
    }

    private void updateCartBadge() {
        if (tvCartBadge == null || cartManager == null) return;
        int count = cartManager.getItemCount();
        if (count > 0) {
            tvCartBadge.setVisibility(View.VISIBLE);
            tvCartBadge.setText(String.valueOf(count));
        } else {
            tvCartBadge.setVisibility(View.GONE);
        }
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_home) {
                    return true;
                } else if (itemId == R.id.navigation_categories) {
                    Intent intent = new Intent(UserMainActivity.this, CategoriesActivity.class);
                    intent.putExtra("user_id", userId);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.navigation_orders) {
                    Intent intent = new Intent(UserMainActivity.this, UserOrdersActivity.class);
                    intent.putExtra("user_id", userId);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.navigation_profile) {
                    Intent intent = new Intent(UserMainActivity.this, ProfileActivity.class);
                    intent.putExtra("user_id", userId);
                    startActivity(intent);
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }
}