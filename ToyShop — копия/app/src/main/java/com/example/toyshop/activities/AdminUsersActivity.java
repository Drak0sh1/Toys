package com.example.toyshop.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toyshop.R;
import com.example.toyshop.adapters.UserAdapter;
import com.example.toyshop.database.DatabaseManager;
import com.example.toyshop.models.User;

import java.util.ArrayList;
import java.util.List;

public class AdminUsersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();
    private TextView tvEmptyView;

    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);

        databaseManager = DatabaseManager.getInstance(this);

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadUsers();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        tvEmptyView = findViewById(R.id.tvEmptyView);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Пользователи");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(this, userList);

        userAdapter.setOnUserClickListener(new UserAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(User user) {
                showUserDetails(user);
            }

            @Override
            public void onDeleteClick(User user) {
                showDeleteConfirmation(user);
            }
        });

        recyclerView.setAdapter(userAdapter);
    }

    private void loadUsers() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<User> users = databaseManager.getDatabaseHelper().getAllUsers();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        userList.clear();
                        userList.addAll(users);
                        userAdapter.notifyDataSetChanged();

                        if (userList.isEmpty()) {
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

    private void showUserDetails(User user) {
        StringBuilder details = new StringBuilder();
        details.append("ID: ").append(user.getUserId()).append("\n\n");
        details.append("Имя: ").append(user.getFullName()).append("\n");
        details.append("Email: ").append(user.getEmail()).append("\n");
        details.append("Телефон: ").append(user.getPhone() != null ? user.getPhone() : "не указан").append("\n");
        details.append("Роль: ").append(user.getRole()).append("\n");
        details.append("Дата регистрации: ").append(new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm")
                .format(new java.util.Date(user.getCreatedAt()))).append("\n");

        new AlertDialog.Builder(this)
                .setTitle("Данные пользователя")
                .setMessage(details.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    private void showDeleteConfirmation(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Удаление пользователя")
                .setMessage("Вы уверены, что хотите удалить пользователя " + user.getFullName() + "?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    // В реальном проекте здесь должно быть удаление
                    Toast.makeText(this, "Удаление пользователей временно отключено", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }
}