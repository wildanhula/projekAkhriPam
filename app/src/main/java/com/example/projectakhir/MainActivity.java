package com.example.projectakhir;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi semua RecyclerView
        RecyclerView recyclerCategories = findViewById(R.id.recyclerCategories);
        RecyclerView recyclerExploreFood = findViewById(R.id.recyclerExploreFood);
        RecyclerView recyclerMostlyVisited = findViewById(R.id.recyclerMostlyVisited);

        // Mengatur layout manager
        recyclerCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerExploreFood.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerMostlyVisited.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Data untuk kategori menggunakan ArrayList dan add()
        List<CategoryModel> categoryList = new ArrayList<>();
        categoryList.add(new CategoryModel(R.drawable.fast_food, "Fast Food"));
        categoryList.add(new CategoryModel(R.drawable.coffee, "Coffee"));
        categoryList.add(new CategoryModel(R.drawable.restaurant, "Restaurant"));
        categoryList.add(new CategoryModel(R.drawable.pasta, "Pasta"));
        categoryList.add(new CategoryModel(R.drawable.dessert, "Dessert"));
        categoryList.add(new CategoryModel(R.drawable.noodle, "Noodle"));

        // Data untuk makanan
        List<FoodModel> foodList = Arrays.asList(
                new FoodModel(R.drawable.restoran, "Spill Pasta", "Pasta dengan berbagai saus spesial"),
                new FoodModel(R.drawable.restoran, "Resto Jepang", "Aneka sushi dan ramen khas Jepang"),
                new FoodModel(R.drawable.restoran, "Pizza Hut", "Pizza lezat dengan berbagai topping"),
                new FoodModel(R.drawable.restoran, "Bakso Malang", "Bakso kuah khas Malang dengan pangsit"),
                new FoodModel(R.drawable.restoran, "Ayam Bakar", "Ayam bakar dengan bumbu rempah"),
                new FoodModel(R.drawable.restoran, "Sate Madura", "Sate ayam khas Madura dengan bumbu kacang")
        );

        CategoryAdapter categoryAdapter = new CategoryAdapter(this, categoryList);
        FoodAdapter foodAdapter = new FoodAdapter(this, foodList);

        recyclerCategories.setAdapter(categoryAdapter);
        recyclerExploreFood.setAdapter(foodAdapter);
        recyclerMostlyVisited.setAdapter(foodAdapter);

        ImageView imgSave = findViewById(R.id.imgSave);
        imgSave.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
            startActivity(intent);
        });

        ImageView profileIcon = findViewById(R.id.profile_icon);
        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfilActivity.class);
            startActivity(intent);
        });
    }
}