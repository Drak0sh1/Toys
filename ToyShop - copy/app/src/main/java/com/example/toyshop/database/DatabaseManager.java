package com.example.toyshop.database;

import android.content.Context;

public class DatabaseManager {
    private static DatabaseManager instance;
    private DatabaseHelper databaseHelper;

    private DatabaseManager(Context context) {
        databaseHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public static synchronized DatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context);
        }
        return instance;
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DatabaseManager must be initialized with context first");
        }
        return instance;
    }

    public DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }
}