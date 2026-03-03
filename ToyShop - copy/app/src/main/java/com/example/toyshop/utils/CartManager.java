package com.example.toyshop.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.toyshop.models.CartItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<CartItem> cartItems;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    private static final String CART_PREFS = "cart_prefs";
    private static final String CART_KEY = "cart_key";

    private CartManager(Context context) {
        sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
        gson = new Gson();
        loadCart();
    }

    public static synchronized CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context.getApplicationContext());
        }
        return instance;
    }

    public static CartManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("CartManager must be initialized with context first");
        }
        return instance;
    }

    private void loadCart() {
        String json = sharedPreferences.getString(CART_KEY, "");
        if (!json.isEmpty()) {
            Type type = new TypeToken<List<CartItem>>(){}.getType();
            cartItems = gson.fromJson(json, type);
        } else {
            cartItems = new ArrayList<>();
        }
    }

    private void saveCart() {
        String json = gson.toJson(cartItems);
        sharedPreferences.edit().putString(CART_KEY, json).apply();
    }

    public void addItem(CartItem item) {
        for (CartItem existingItem : cartItems) {
            if (existingItem.getToyId().equals(item.getToyId())) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                saveCart();
                return;
            }
        }
        cartItems.add(item);
        saveCart();
    }

    public void removeItem(String toyId) {
        cartItems.removeIf(item -> item.getToyId().equals(toyId));
        saveCart();
    }

    public void updateQuantity(String toyId, int quantity) {
        CartItem toRemove = null;
        for (CartItem item : cartItems) {
            if (item.getToyId().equals(toyId)) {
                if (quantity <= 0) {
                    toRemove = item;
                } else {
                    item.setQuantity(quantity);
                }
                break;
            }
        }
        if (toRemove != null) {
            cartItems.remove(toRemove);
        }
        saveCart();
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public void clearCart() {
        cartItems.clear();
        saveCart();
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public int getItemCount() {
        int count = 0;
        for (CartItem item : cartItems) {
            count += item.getQuantity();
        }
        return count;
    }

    public boolean isEmpty() {
        return cartItems.isEmpty();
    }
}