package com.example.toyshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.toyshop.R;
import com.example.toyshop.database.DatabaseManager;
import com.example.toyshop.models.CartItem;
import com.example.toyshop.models.Order;
import com.example.toyshop.utils.CartManager;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CheckoutActivity extends AppCompatActivity {

    private TextView tvTotalAmount, tvItemsList;
    private EditText etAddress, etPhone, etComment;
    private Spinner spinnerPaymentMethod;
    private Button btnPlaceOrder;

    private String userId;
    private CartManager cartManager;
    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        userId = getIntent().getStringExtra("user_id");
        cartManager = CartManager.getInstance(this);
        databaseManager = DatabaseManager.getInstance(this);

        initViews();
        setupToolbar();
        setupPaymentSpinner();
        displayOrderSummary();
        setupClickListeners();
    }

    private void initViews() {
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvItemsList = findViewById(R.id.tvItemsList);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        etComment = findViewById(R.id.etComment);
        spinnerPaymentMethod = findViewById(R.id.spinnerPaymentMethod);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Оформление заказа");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupPaymentSpinner() {
        String[] paymentMethods = {"Наличными при получении", "Картой при получении", "Онлайн картой"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, paymentMethods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentMethod.setAdapter(adapter);
    }

    private void displayOrderSummary() {
        List<CartItem> items = cartManager.getCartItems();
        StringBuilder itemsText = new StringBuilder();

        for (CartItem item : items) {
            itemsText.append(item.getToyName())
                    .append(" x")
                    .append(item.getQuantity())
                    .append(" - ")
                    .append(String.format("%.2f ₽", item.getTotalPrice()))
                    .append("\n");
        }

        tvItemsList.setText(itemsText.toString());

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("ru", "RU"));
        tvTotalAmount.setText("Итого: " + format.format(cartManager.getTotalPrice()));
    }

    private void setupClickListeners() {
        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
            }
        });
    }

    private void placeOrder() {
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String comment = etComment.getText().toString().trim();
        String paymentMethod = spinnerPaymentMethod.getSelectedItem().toString();

        if (address.isEmpty()) {
            etAddress.setError("Введите адрес доставки");
            return;
        }

        if (phone.isEmpty()) {
            etPhone.setError("Введите телефон");
            return;
        }

        btnPlaceOrder.setEnabled(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Создаем заказ
                Order order = new Order();
                order.setOrderId(UUID.randomUUID().toString());
                order.setUserId(userId);
                order.setItems(cartManager.getCartItems());
                order.setTotalAmount(cartManager.getTotalPrice());
                order.setStatus("pending");
                order.setShippingAddress(address);
                order.setPaymentMethod(paymentMethod);
                order.setOrderDate(System.currentTimeMillis());

                boolean success = databaseManager.getDatabaseHelper().createOrder(order);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnPlaceOrder.setEnabled(true);

                        if (success) {
                            // Очищаем корзину
                            cartManager.clearCart();

                            Toast.makeText(CheckoutActivity.this,
                                    "Заказ успешно оформлен!", Toast.LENGTH_LONG).show();

                            // Переходим на экран заказов
                            Intent intent = new Intent(CheckoutActivity.this, UserOrdersActivity.class);
                            intent.putExtra("user_id", userId);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(CheckoutActivity.this,
                                    "Ошибка при оформлении заказа", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }
}