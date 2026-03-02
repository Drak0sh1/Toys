package com.example.toyshop.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.toyshop.models.CartItem;
import com.example.toyshop.models.Order;
import com.example.toyshop.models.Toy;
import com.example.toyshop.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "toystore.db";
    private static final int DATABASE_VERSION = 1;

    // Таблица пользователей
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_FULL_NAME = "full_name";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_ROLE = "role";
    private static final String COLUMN_PROFILE_IMAGE = "profile_image";
    private static final String COLUMN_CREATED_AT = "created_at";

    // Таблица игрушек
    private static final String TABLE_TOYS = "toys";
    private static final String COLUMN_TOY_ID = "toy_id";
    private static final String COLUMN_TOY_NAME = "toy_name";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_AGE_GROUP = "age_group";
    private static final String COLUMN_IMAGE_URL = "image_url";
    private static final String COLUMN_STOCK_QUANTITY = "stock_quantity";
    private static final String COLUMN_IS_AVAILABLE = "is_available";
    private static final String COLUMN_TOY_CREATED_AT = "toy_created_at";

    // Таблица заказов
    private static final String TABLE_ORDERS = "orders";
    private static final String COLUMN_ORDER_ID = "order_id";
    private static final String COLUMN_ORDER_USER_ID = "user_id";
    private static final String COLUMN_ITEMS_JSON = "items_json";
    private static final String COLUMN_TOTAL_AMOUNT = "total_amount";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_SHIPPING_ADDRESS = "shipping_address";
    private static final String COLUMN_PAYMENT_METHOD = "payment_method";
    private static final String COLUMN_ORDER_DATE = "order_date";
    private static final String COLUMN_DELIVERY_DATE = "delivery_date";

    private Gson gson = new Gson();

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создание таблицы пользователей
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " TEXT PRIMARY KEY, "
                + COLUMN_EMAIL + " TEXT UNIQUE, "
                + COLUMN_PASSWORD + " TEXT, "
                + COLUMN_FULL_NAME + " TEXT, "
                + COLUMN_PHONE + " TEXT, "
                + COLUMN_ROLE + " TEXT, "
                + COLUMN_PROFILE_IMAGE + " TEXT, "
                + COLUMN_CREATED_AT + " INTEGER"
                + ")";
        db.execSQL(createUsersTable);

        // Создание таблицы игрушек
        String createToysTable = "CREATE TABLE " + TABLE_TOYS + "("
                + COLUMN_TOY_ID + " TEXT PRIMARY KEY, "
                + COLUMN_TOY_NAME + " TEXT, "
                + COLUMN_DESCRIPTION + " TEXT, "
                + COLUMN_PRICE + " REAL, "
                + COLUMN_CATEGORY + " TEXT, "
                + COLUMN_AGE_GROUP + " INTEGER, "
                + COLUMN_IMAGE_URL + " TEXT, "
                + COLUMN_STOCK_QUANTITY + " INTEGER, "
                + COLUMN_IS_AVAILABLE + " INTEGER, "
                + COLUMN_TOY_CREATED_AT + " INTEGER"
                + ")";
        db.execSQL(createToysTable);

        // Создание таблицы заказов
        String createOrdersTable = "CREATE TABLE " + TABLE_ORDERS + "("
                + COLUMN_ORDER_ID + " TEXT PRIMARY KEY, "
                + COLUMN_ORDER_USER_ID + " TEXT, "
                + COLUMN_ITEMS_JSON + " TEXT, "
                + COLUMN_TOTAL_AMOUNT + " REAL, "
                + COLUMN_STATUS + " TEXT, "
                + COLUMN_SHIPPING_ADDRESS + " TEXT, "
                + COLUMN_PAYMENT_METHOD + " TEXT, "
                + COLUMN_ORDER_DATE + " INTEGER, "
                + COLUMN_DELIVERY_DATE + " INTEGER, "
                + "FOREIGN KEY(" + COLUMN_ORDER_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")"
                + ")";
        db.execSQL(createOrdersTable);

        // Добавляем тестового администратора
        addDefaultAdmin(db);

        // Добавляем тестовые игрушки
        addDefaultToys(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOYS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        onCreate(db);
    }

    private void addDefaultAdmin(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, "admin_001");
        values.put(COLUMN_EMAIL, "admin@toystore.com");
        values.put(COLUMN_PASSWORD, "admin123"); // В реальном проекте нужно хэшировать
        values.put(COLUMN_FULL_NAME, "Администратор");
        values.put(COLUMN_PHONE, "+79999999999");
        values.put(COLUMN_ROLE, "admin");
        values.put(COLUMN_CREATED_AT, System.currentTimeMillis());

        db.insert(TABLE_USERS, null, values);
    }

    private void addDefaultToys(SQLiteDatabase db) {
        addToy(db, new Toy("1", "Конструктор LEGO City", "Большой конструктор для города", 2999.99, "Конструкторы", 6, 15));
        addToy(db, new Toy("2", "Мягкая игрушка Мишка", "Пушистый мишка 30 см", 1299.50, "Мягкие игрушки", 3, 25));
        addToy(db, new Toy("3", "Машинка радиоуправляемая", "Скоростная машинка", 3499.00, "Машинки", 5, 8));
        addToy(db, new Toy("4", "Набор доктора", "Игровой набор с инструментами", 899.99, "Ролевые игры", 4, 12));
        addToy(db, new Toy("5", "Кукла Барби", "Кукла с аксессуарами", 2499.00, "Куклы", 5, 10));
        addToy(db, new Toy("6", "Развивающая доска", "Бизиборд для малышей", 1999.00, "Развивающие", 2, 7));
    }

    private void addToy(SQLiteDatabase db, Toy toy) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TOY_ID, toy.getToyId());
        values.put(COLUMN_TOY_NAME, toy.getName());
        values.put(COLUMN_DESCRIPTION, toy.getDescription());
        values.put(COLUMN_PRICE, toy.getPrice());
        values.put(COLUMN_CATEGORY, toy.getCategory());
        values.put(COLUMN_AGE_GROUP, toy.getAgeGroup());
        values.put(COLUMN_STOCK_QUANTITY, toy.getStockQuantity());
        values.put(COLUMN_IS_AVAILABLE, toy.isAvailable() ? 1 : 0);
        values.put(COLUMN_TOY_CREATED_AT, System.currentTimeMillis());

        db.insert(TABLE_TOYS, null, values);
    }

    // ============= МЕТОДЫ ДЛЯ ПОЛЬЗОВАТЕЛЕЙ =============

    public boolean registerUser(User user, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, user.getUserId());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, password); // В реальном проекте нужно хэшировать
        values.put(COLUMN_FULL_NAME, user.getFullName());
        values.put(COLUMN_PHONE, user.getPhone());
        values.put(COLUMN_ROLE, user.getRole());
        values.put(COLUMN_CREATED_AT, user.getCreatedAt());

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public User loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS,
                null,
                COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{email, password},
                null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
            user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME)));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)));
            user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE)));
            user.setProfileImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_IMAGE)));
            user.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));
            cursor.close();
        }
        db.close();
        return user;
    }

    public User getUserById(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS,
                null,
                COLUMN_USER_ID + "=?",
                new String[]{userId},
                null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
            user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME)));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)));
            user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE)));
            user.setProfileImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_IMAGE)));
            user.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));
            cursor.close();
        }
        db.close();
        return user;
    }

    public boolean updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_FULL_NAME, user.getFullName());
        values.put(COLUMN_PHONE, user.getPhone());
        values.put(COLUMN_PROFILE_IMAGE, user.getProfileImageUrl());

        int result = db.update(TABLE_USERS, values, COLUMN_USER_ID + "=?", new String[]{user.getUserId()});
        db.close();
        return result > 0;
    }

    public String getUserRole(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ROLE},
                COLUMN_USER_ID + "=?",
                new String[]{userId},
                null, null, null);

        String role = "user";
        if (cursor != null && cursor.moveToFirst()) {
            role = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE));
            cursor.close();
        }
        db.close();
        return role;
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS,
                null,
                null, null, null, null, COLUMN_CREATED_AT + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
                user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME)));
                user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)));
                user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE)));
                user.setProfileImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_IMAGE)));
                user.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));
                userList.add(user);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return userList;
    }

    // ============= МЕТОДЫ ДЛЯ ИГРУШЕК =============

    public boolean addToy(Toy toy) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (toy.getToyId() == null || toy.getToyId().isEmpty()) {
            toy.setToyId(String.valueOf(System.currentTimeMillis()));
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_TOY_ID, toy.getToyId());
        values.put(COLUMN_TOY_NAME, toy.getName());
        values.put(COLUMN_DESCRIPTION, toy.getDescription());
        values.put(COLUMN_PRICE, toy.getPrice());
        values.put(COLUMN_CATEGORY, toy.getCategory());
        values.put(COLUMN_AGE_GROUP, toy.getAgeGroup());
        values.put(COLUMN_IMAGE_URL, toy.getImageUrl());
        values.put(COLUMN_STOCK_QUANTITY, toy.getStockQuantity());
        values.put(COLUMN_IS_AVAILABLE, toy.isAvailable() ? 1 : 0);
        values.put(COLUMN_TOY_CREATED_AT, toy.getCreatedAt() > 0 ? toy.getCreatedAt() : System.currentTimeMillis());

        long result = db.insert(TABLE_TOYS, null, values);
        db.close();
        return result != -1;
    }

    public boolean updateToy(Toy toy) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TOY_NAME, toy.getName());
        values.put(COLUMN_DESCRIPTION, toy.getDescription());
        values.put(COLUMN_PRICE, toy.getPrice());
        values.put(COLUMN_CATEGORY, toy.getCategory());
        values.put(COLUMN_AGE_GROUP, toy.getAgeGroup());
        values.put(COLUMN_IMAGE_URL, toy.getImageUrl());
        values.put(COLUMN_STOCK_QUANTITY, toy.getStockQuantity());
        values.put(COLUMN_IS_AVAILABLE, toy.isAvailable() ? 1 : 0);

        int result = db.update(TABLE_TOYS, values, COLUMN_TOY_ID + "=?", new String[]{toy.getToyId()});
        db.close();
        return result > 0;
    }

    public boolean deleteToy(String toyId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_TOYS, COLUMN_TOY_ID + "=?", new String[]{toyId});
        db.close();
        return result > 0;
    }

    public List<Toy> getAllToys() {
        List<Toy> toyList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TOYS,
                null,
                null, null, null, null, COLUMN_TOY_CREATED_AT + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Toy toy = new Toy();
                toy.setToyId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TOY_ID)));
                toy.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TOY_NAME)));
                toy.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                toy.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)));
                toy.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)));
                toy.setAgeGroup(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE_GROUP)));
                toy.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)));
                toy.setStockQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STOCK_QUANTITY)));
                toy.setAvailable(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_AVAILABLE)) == 1);
                toy.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TOY_CREATED_AT)));
                toyList.add(toy);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return toyList;
    }

    public Toy getToyById(String toyId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TOYS,
                null,
                COLUMN_TOY_ID + "=?",
                new String[]{toyId},
                null, null, null);

        Toy toy = null;
        if (cursor != null && cursor.moveToFirst()) {
            toy = new Toy();
            toy.setToyId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TOY_ID)));
            toy.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TOY_NAME)));
            toy.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
            toy.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)));
            toy.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)));
            toy.setAgeGroup(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE_GROUP)));
            toy.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)));
            toy.setStockQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STOCK_QUANTITY)));
            toy.setAvailable(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_AVAILABLE)) == 1);
            toy.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TOY_CREATED_AT)));
            cursor.close();
        }
        db.close();
        return toy;
    }

    public List<Toy> getToysByCategory(String category) {
        List<Toy> toyList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TOYS,
                null,
                COLUMN_CATEGORY + "=?",
                new String[]{category},
                null, null, COLUMN_TOY_CREATED_AT + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Toy toy = new Toy();
                toy.setToyId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TOY_ID)));
                toy.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TOY_NAME)));
                toy.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                toy.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)));
                toy.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)));
                toy.setAgeGroup(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE_GROUP)));
                toy.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)));
                toy.setStockQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STOCK_QUANTITY)));
                toy.setAvailable(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_AVAILABLE)) == 1);
                toy.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TOY_CREATED_AT)));
                toyList.add(toy);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return toyList;
    }

    public boolean updateStock(String toyId, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_STOCK_QUANTITY, newQuantity);
        values.put(COLUMN_IS_AVAILABLE, newQuantity > 0 ? 1 : 0);

        int result = db.update(TABLE_TOYS, values, COLUMN_TOY_ID + "=?", new String[]{toyId});
        db.close();
        return result > 0;
    }

    // ============= МЕТОДЫ ДЛЯ ЗАКАЗОВ =============

    public boolean createOrder(Order order) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (order.getOrderId() == null || order.getOrderId().isEmpty()) {
            order.setOrderId(String.valueOf(System.currentTimeMillis()));
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_ID, order.getOrderId());
        values.put(COLUMN_ORDER_USER_ID, order.getUserId());
        values.put(COLUMN_ITEMS_JSON, gson.toJson(order.getItems()));
        values.put(COLUMN_TOTAL_AMOUNT, order.getTotalAmount());
        values.put(COLUMN_STATUS, order.getStatus() != null ? order.getStatus() : "pending");
        values.put(COLUMN_SHIPPING_ADDRESS, order.getShippingAddress());
        values.put(COLUMN_PAYMENT_METHOD, order.getPaymentMethod());
        values.put(COLUMN_ORDER_DATE, order.getOrderDate() > 0 ? order.getOrderDate() : System.currentTimeMillis());
        values.put(COLUMN_DELIVERY_DATE, order.getDeliveryDate());

        long result = db.insert(TABLE_ORDERS, null, values);

        // Уменьшаем количество товаров на складе
        if (result != -1 && order.getItems() != null) {
            for (CartItem item : order.getItems()) {
                Toy toy = getToyById(item.getToyId());
                if (toy != null) {
                    int newStock = toy.getStockQuantity() - item.getQuantity();
                    updateStock(item.getToyId(), newStock);
                }
            }
        }

        db.close();
        return result != -1;
    }

    public List<Order> getUserOrders(String userId) {
        List<Order> orderList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ORDERS,
                null,
                COLUMN_ORDER_USER_ID + "=?",
                new String[]{userId},
                null, null, COLUMN_ORDER_DATE + " DESC");

        orderList = extractOrdersFromCursor(cursor);
        db.close();
        return orderList;
    }

    public List<Order> getAllOrders() {
        List<Order> orderList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ORDERS,
                null,
                null, null, null, null, COLUMN_ORDER_DATE + " DESC");

        orderList = extractOrdersFromCursor(cursor);
        db.close();
        return orderList;
    }

    private List<Order> extractOrdersFromCursor(Cursor cursor) {
        List<Order> orderList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            Type type = new TypeToken<List<CartItem>>(){}.getType();

            do {
                Order order = new Order();
                order.setOrderId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_ID)));
                order.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_USER_ID)));

                String itemsJson = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEMS_JSON));
                List<CartItem> items = gson.fromJson(itemsJson, type);
                order.setItems(items);

                order.setTotalAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_AMOUNT)));
                order.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
                order.setShippingAddress(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SHIPPING_ADDRESS)));
                order.setPaymentMethod(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_METHOD)));
                order.setOrderDate(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DATE)));
                order.setDeliveryDate(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DELIVERY_DATE)));

                orderList.add(order);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return orderList;
    }

    public boolean updateOrderStatus(String orderId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);

        int result = db.update(TABLE_ORDERS, values, COLUMN_ORDER_ID + "=?", new String[]{orderId});
        db.close();
        return result > 0;
    }

    public Order getOrderById(String orderId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ORDERS,
                null,
                COLUMN_ORDER_ID + "=?",
                new String[]{orderId},
                null, null, null);

        List<Order> orders = extractOrdersFromCursor(cursor);
        db.close();

        return orders.isEmpty() ? null : orders.get(0);
    }
}