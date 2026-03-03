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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toyshop.R;
import com.example.toyshop.adapters.ToyAdapter;
import com.example.toyshop.database.DatabaseManager;
import com.example.toyshop.models.Toy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AdminMainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ToyAdapter toyAdapter;
    private List<Toy> toyList = new ArrayList<>();
    private TextView tvEmptyView;
    private FloatingActionButton fabAddToy;
    private String userId;

    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        userId = getIntent().getStringExtra("user_id");
        if (userId == null) {
            // Если userId не передан, пробуем получить из SharedPreferences
            SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
            userId = prefs.getString("user_id", null);
        }

        databaseManager = DatabaseManager.getInstance(this);

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadToys();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        tvEmptyView = findViewById(R.id.tvEmptyView);
        fabAddToy = findViewById(R.id.fabAddToy);

        fabAddToy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, AddEditToyActivity.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Панель администратора");
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        toyAdapter = new ToyAdapter(this, toyList, true);

        toyAdapter.setOnItemClickListener(new ToyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Toy toy) {
                Intent intent = new Intent(AdminMainActivity.this, ToyDetailActivity.class);
                intent.putExtra("toy_id", toy.getToyId());
                intent.putExtra("user_id", userId);
                startActivity(intent);
            }

            @Override
            public void onEditClick(Toy toy) {
                Intent intent = new Intent(AdminMainActivity.this, AddEditToyActivity.class);
                intent.putExtra("toy_id", toy.getToyId());
                intent.putExtra("user_id", userId);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Toy toy) {
                deleteToy(toy);
            }

            @Override
            public void onAddToCartClick(Toy toy) {
                // Не используется для админа
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

                        if (toyList.isEmpty()) {
                            tvEmptyView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            tvEmptyView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }).start();
    }

    private void deleteToy(Toy toy) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = databaseManager.getDatabaseHelper().deleteToy(toy.getToyId());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (success) {
                            Toast.makeText(AdminMainActivity.this, "Товар удален", Toast.LENGTH_SHORT).show();
                            loadToys();
                        } else {
                            Toast.makeText(AdminMainActivity.this, "Ошибка удаления", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_orders) {
            Intent intent = new Intent(AdminMainActivity.this, AdminOrdersActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_users) {
            Intent intent = new Intent(AdminMainActivity.this, AdminUsersActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_profile) {
            Intent intent = new Intent(AdminMainActivity.this, ProfileActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent intent = new Intent(AdminMainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadToys();
    }
}