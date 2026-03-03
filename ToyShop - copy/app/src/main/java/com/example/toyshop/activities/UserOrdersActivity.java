package com.example.toyshop.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toyshop.R;
import com.example.toyshop.adapters.OrderAdapter;
import com.example.toyshop.database.DatabaseManager;
import com.example.toyshop.models.Order;

import java.util.ArrayList;
import java.util.List;

public class UserOrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList = new ArrayList<>();
    private TextView tvEmptyView;
    private String userId;

    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        userId = getIntent().getStringExtra("user_id");
        databaseManager = DatabaseManager.getInstance(this);

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadOrders();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        tvEmptyView = findViewById(R.id.tvEmptyView);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Мои заказы");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(this, orderList, false);

        orderAdapter.setOnOrderClickListener(new OrderAdapter.OnOrderClickListener() {
            @Override
            public void onOrderClick(Order order) {
                // Просмотр деталей заказа
                showOrderDetails(order);
            }

            @Override
            public void onUpdateStatusClick(Order order) {
                // Не используется для пользователя
            }
        });

        recyclerView.setAdapter(orderAdapter);
    }

    private void loadOrders() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Order> orders = databaseManager.getDatabaseHelper().getUserOrders(userId);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        orderList.clear();
                        orderList.addAll(orders);
                        orderAdapter.notifyDataSetChanged();

                        if (orderList.isEmpty()) {
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

    private void showOrderDetails(Order order) {
        // Создаем диалог с деталями заказа
        StringBuilder details = new StringBuilder();
        details.append("Заказ #").append(order.getOrderId().substring(0, 8)).append("\n\n");
        details.append("Статус: ").append(getStatusText(order.getStatus())).append("\n");
        details.append("Дата: ").append(new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm")
                .format(new java.util.Date(order.getOrderDate()))).append("\n\n");
        details.append("Товары:\n");

        for (com.example.toyshop.models.CartItem item : order.getItems()) {
            details.append("• ").append(item.getToyName())
                    .append(" x").append(item.getQuantity())
                    .append(" - ").append(String.format("%.2f ₽", item.getTotalPrice()))
                    .append("\n");
        }

        details.append("\nИтого: ").append(String.format("%.2f ₽", order.getTotalAmount())).append("\n\n");
        details.append("Адрес доставки: ").append(order.getShippingAddress()).append("\n");
        details.append("Способ оплаты: ").append(order.getPaymentMethod());

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Детали заказа")
                .setMessage(details.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    private String getStatusText(String status) {
        switch (status) {
            case "pending": return "Ожидает обработки";
            case "processing": return "В обработке";
            case "shipped": return "Отправлен";
            case "delivered": return "Доставлен";
            case "cancelled": return "Отменен";
            default: return status;
        }
    }
}