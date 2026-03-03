package com.example.toyshop.models;

public class CartItem {
    private String toyId;
    private String toyName;
    private double price;
    private int quantity;
    private String imageUrl;

    public CartItem() {
    }

    public CartItem(String toyId, String toyName, double price, int quantity, String imageUrl) {
        this.toyId = toyId;
        this.toyName = toyName;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    // Геттеры и сеттеры
    public String getToyId() { return toyId; }
    public void setToyId(String toyId) { this.toyId = toyId; }

    public String getToyName() { return toyName; }
    public void setToyName(String toyName) { this.toyName = toyName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double getTotalPrice() {
        return price * quantity;
    }
}