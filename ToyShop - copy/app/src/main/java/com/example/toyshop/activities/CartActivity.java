package com.example.toyshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toyshop.R;
import com.example.toyshop.adapters.CartAdapter;
import com.example.toyshop.models.CartItem;
import com.example.toyshop.utils.CartManager;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private TextView tvEmptyCart, tvTotalPrice, tvTotalItems;
    private Button btnCheckout, btnContinueShopping;
    private String userId;

    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        userId = getIntent().getStringExtra("user_id");
        cartManager = CartManager.getInstance(this);

        initViews();
        setupToolbar();
        setupRecyclerView();
        updateCartDisplay();
        setupClickListeners();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        tvEmptyCart = findViewById(R.id.tvEmptyCart);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvTotalItems = findViewById(R.id.tvTotalItems);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnContinueShopping = findViewById(R.id.btnContinueShopping);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Корзина");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, cartManager.getCartItems());

        cartAdapter.setOnCartItemClickListener(new CartAdapter.OnCartItemClickListener() {
            @Override
            public void onQuantityChanged(CartItem item, int newQuantity) {
                cartManager.updateQuantity(item.getToyId(), newQuantity);
                updateCartDisplay();
            }

            @Override
            public void onRemoveClick(CartItem item) {
                cartManager.removeItem(item.getToyId());
                updateCartDisplay();
                Toast.makeText(CartActivity.this, "Товар удален из корзины", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(cartAdapter);
    }

    private void updateCartDisplay() {
        List<CartItem> items = cartManager.getCartItems();
        cartAdapter.updateItems(items);

        if (items.isEmpty()) {
            tvEmptyCart.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            btnCheckout.setEnabled(false);
        } else {
            tvEmptyCart.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            btnCheckout.setEnabled(true);
        }

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("ru", "RU"));
        tvTotalPrice.setText("Итого: " + format.format(cartManager.getTotalPrice()));

        int itemCount = cartManager.getItemCount();
        tvTotalItems.setText("Товаров: " + itemCount);
    }

    private void setupClickListeners() {
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);
            }
        });

        btnContinueShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartDisplay();
    }
}