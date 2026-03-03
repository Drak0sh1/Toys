package com.example.toyshop.models;

public class Toy {
    private String toyId;
    private String name;
    private String description;
    private double price;
    private String category;
    private int ageGroup;
    private String imageUrl;
    private int stockQuantity;
    private boolean isAvailable;
    private long createdAt;

    public Toy() {
        // Пустой конструктор для Firebase
    }

    public Toy(String toyId, String name, String description, double price,
               String category, int ageGroup, int stockQuantity) {
        this.toyId = toyId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.ageGroup = ageGroup;
        this.stockQuantity = stockQuantity;
        this.isAvailable = stockQuantity > 0;
        this.createdAt = System.currentTimeMillis();
    }

    // Геттеры и сеттеры
    public String getToyId() { return toyId; }
    public void setToyId(String toyId) { this.toyId = toyId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getAgeGroup() { return ageGroup; }
    public void setAgeGroup(int ageGroup) { this.ageGroup = ageGroup; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
        this.isAvailable = stockQuantity > 0;
    }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}