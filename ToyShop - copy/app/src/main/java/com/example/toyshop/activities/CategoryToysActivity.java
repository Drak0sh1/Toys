package com.example.toyshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

public class CategoryToysActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ToyAdapter toyAdapter;
    private List<Toy> toyList = new ArrayList<>();
    private TextView tvEmptyView;
    private String userId;
    private String category;

    private DatabaseManager databaseManager;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        userId = getIntent().getStringExtra("user_id");
        category = getIntent().getStringExtra("category");
        if (category == null) {
            category = "";
        }

        databaseManager = DatabaseManager.getInstance(this);
        cartManager = CartManager.getInstance(this);

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadToysByCategory();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        tvEmptyView = findViewById(R.id.tvEmptyView);
        if (tvEmptyView != null) {
            tvEmptyView.setText("Нет товаров в этой категории");
        }
        View fabAddToy = findViewById(R.id.fabAddToy);
        if (fabAddToy != null) {
            fabAddToy.setVisibility(View.GONE);
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(category.isEmpty() ? "Товары" : category);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        toyAdapter = new ToyAdapter(this, toyList, false);

        toyAdapter.setOnItemClickListener(new ToyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Toy toy) {
                Intent intent = new Intent(CategoryToysActivity.this, ToyDetailActivity.class);
                intent.putExtra("toy_id", toy.getToyId());
                intent.putExtra("user_id", userId);
                intent.putExtra("user_role", "user");
                startActivity(intent);
            }

            @Override
            public void onEditClick(Toy toy) {}

            @Override
            public void onDeleteClick(Toy toy) {}

            @Override
            public void onAddToCartClick(Toy toy) {
                if (!toy.isAvailable() || toy.getStockQuantity() <= 0) {
                    Toast.makeText(CategoryToysActivity.this, "Товар недоступен", Toast.LENGTH_SHORT).show();
                    return;
                }
                CartItem item = new CartItem(
                        toy.getToyId(),
                        toy.getName(),
                        toy.getPrice(),
                        1,
                        toy.getImageUrl(),
                        toy.getStockQuantity()
                );
                cartManager.addItem(item);
                Toast.makeText(CategoryToysActivity.this, "Товар добавлен в корзину", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(toyAdapter);
    }

    private void loadToysByCategory() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Toy> toys = category.isEmpty()
                        ? databaseManager.getDatabaseHelper().getAllToys()
                        : databaseManager.getDatabaseHelper().getToysByCategory(category);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toyList.clear();
                        toyList.addAll(toys);
                        toyAdapter.notifyDataSetChanged();

                        if (tvEmptyView != null) {
                            if (toyList.isEmpty()) {
                                tvEmptyView.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                tvEmptyView.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }
        }).start();
    }
}
