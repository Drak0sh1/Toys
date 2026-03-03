package com.example.toyshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.toyshop.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoriesActivity extends AppCompatActivity {

    private GridView gridView;
    private TextView tvEmptyView;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        userId = getIntent().getStringExtra("user_id");

        initViews();
        setupToolbar();
        setupCategories();
    }

    private void initViews() {
        gridView = findViewById(R.id.gridView);
        tvEmptyView = findViewById(R.id.tvEmptyView);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Категории товаров");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupCategories() {
        String[] categories = {
                "Конструкторы", "Мягкие игрушки", "Машинки",
                "Куклы", "Развивающие", "Настольные игры",
                "Роботы", "Железные дороги", "Динозавры",
                "Супергерои", "Принцессы", "Пазлы",
                "Музыкальные", "Спортивные", "Другое"
        };

        int[] icons = {
                R.drawable.ic_category_constructor,
                R.drawable.ic_category_stuffed,
                R.drawable.ic_category_cars,
                R.drawable.ic_category_doll,
                R.drawable.ic_category_educational,
                R.drawable.ic_category_board_games,
                R.drawable.ic_category_robots,
                R.drawable.ic_category_trains,
                R.drawable.ic_category_dinosaurs,
                R.drawable.ic_category_superheroes,
                R.drawable.ic_category_princess,
                R.drawable.ic_category_puzzles,
                R.drawable.ic_category_musical,
                R.drawable.ic_category_sports,
                R.drawable.ic_category_other
        };

        List<Map<String, Object>> categoryList = new ArrayList<>();
        for (int i = 0; i < categories.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", categories[i]);
            item.put("icon", icons[i]);
            categoryList.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                categoryList,
                R.layout.item_category,
                new String[]{"name", "icon"},
                new int[]{R.id.tvCategoryName, R.id.ivCategoryIcon}
        );

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String category = categories[position];
                Intent intent = new Intent(CategoriesActivity.this, CategoryToysActivity.class);
                intent.putExtra("user_id", userId);
                intent.putExtra("category", category);
                startActivity(intent);
            }
        });
    }
}