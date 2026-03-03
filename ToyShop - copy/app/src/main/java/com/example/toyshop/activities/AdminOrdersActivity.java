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

public class AdminOrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList = new ArrayList<>();
    private TextView tvEmptyView;
    private String adminId;

    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        adminId = getIntent().getStringExtra("user_id");
        databaseManager = DatabaseManager.getInstance(this);

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadAllOrders();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        tvEmptyView = findViewById(R.id.tvEmptyView);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Управление заказами");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(this, orderList, true);

        orderAdapter.setOnOrderClickListener(new OrderAdapter.OnOrderClickListener() {
            @Override
            public void onOrderClick(Order order) {
                showOrderDetails(order);
            }

            @Override
            public void onUpdateStatusClick(Order order) {
                showStatusUpdateDialog(order);
            }
        });

        recyclerView.setAdapter(orderAdapter);
    }

    private void loadAllOrders() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Order> orders = databaseManager.getDatabaseHelper().getAllOrders();

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
        StringBuilder details = new StringBuilder();
        details.append("Заказ #").append(order.getOrderId().substring(0, 8)).append("\n\n");
        details.append("ID пользователя: ").append(order.getUserId()).append("\n");
        details.append("Статус: ").append(getStatusText(order.getStatus())).append("\n");
        details.append("Дата: ").append(new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm")
                .format(new java.util.Date(order.getOrderDate()))).append("\n\n");
        details.append("Товары:\n");

        List<com.example.toyshop.models.CartItem> items = order.getItems();
        if (items != null) {
            for (com.example.toyshop.models.CartItem item : items) {
                details.append("• ").append(item.getToyName())
                    .append(" x").append(item.getQuantity())
                    .append(" - ").append(String.format("%.2f ₽", item.getTotalPrice()))
                    .append("\n");
            }
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

    private void showStatusUpdateDialog(Order order) {
        String[] statuses = {"pending", "processing", "shipped", "delivered", "cancelled"};
        String[] statusNames = {"Ожидает обработки", "В обработке", "Отправлен", "Доставлен", "Отменен"};

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Изменить статус заказа")
                .setItems(statusNames, (dialog, which) -> {
                    updateOrderStatus(order.getOrderId(), statuses[which]);
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void updateOrderStatus(String orderId, String newStatus) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = databaseManager.getDatabaseHelper().updateOrderStatus(orderId, newStatus);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (success) {
                            Toast.makeText(AdminOrdersActivity.this,
                                    "Статус заказа обновлен", Toast.LENGTH_SHORT).show();
                            loadAllOrders();
                        } else {
                            Toast.makeText(AdminOrdersActivity.this,
                                    "Ошибка обновления статуса", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
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