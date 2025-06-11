package com.example.projectakhir;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FoodAdapter foodAdapterExplore;
    private FoodAdapter foodAdapterMostly;
    private List<FoodModel> foodListAll;
    private List<FoodModel> foodListMostlyVisited;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerCategories = findViewById(R.id.recyclerCategories);
        RecyclerView recyclerExploreFood = findViewById(R.id.recyclerExploreFood);
        RecyclerView recyclerMostlyVisited = findViewById(R.id.recyclerMostlyVisited);

        recyclerCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerExploreFood.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerMostlyVisited.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Dummy kategori
        List<CategoryModel> categoryList = new ArrayList<>();
        categoryList.add(new CategoryModel(R.drawable.fast_food, "Fast Food"));
        categoryList.add(new CategoryModel(R.drawable.coffee, "Coffee"));
        categoryList.add(new CategoryModel(R.drawable.restaurant, "Restaurant"));
        categoryList.add(new CategoryModel(R.drawable.pasta, "Pasta"));
        categoryList.add(new CategoryModel(R.drawable.dessert, "Dessert"));
        categoryList.add(new CategoryModel(R.drawable.noodle, "Noodle"));
        CategoryAdapter categoryAdapter = new CategoryAdapter(this, categoryList);
        recyclerCategories.setAdapter(categoryAdapter);

        // Adapter dan list untuk Explore dan Mostly Visited
        foodListAll = new ArrayList<>();
        foodListMostlyVisited = new ArrayList<>();
        foodAdapterExplore = new FoodAdapter(this, foodListAll);
        foodAdapterMostly = new FoodAdapter(this, foodListMostlyVisited);

        recyclerExploreFood.setAdapter(foodAdapterExplore);
        recyclerMostlyVisited.setAdapter(foodAdapterMostly);

        // Ambil data dari Firebase
        DatabaseReference foodRef = FirebaseDatabase.getInstance(
                "https://projectakhir-d24d3-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference("foods");

        foodRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodListAll.clear();
                foodListMostlyVisited.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    FoodModel food = data.getValue(FoodModel.class);
                    if (food != null) {
                        foodListAll.add(food);
                        // Kriteria: mostly visited yang judulnya mengandung "Ayam"
                        if (food.getTitle() != null && food.getTitle().toLowerCase().contains("ayam")) {
                            foodListMostlyVisited.add(food);
                        }
                    }
                }
                Collections.shuffle(foodListAll); // Biar Explore acak
                foodAdapterExplore.notifyDataSetChanged();
                foodAdapterMostly.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Gagal load makanan", Toast.LENGTH_SHORT).show();
            }
        });

        // Navigasi
        ImageView imgSave = findViewById(R.id.imgSave);
        imgSave.setOnClickListener(v -> startActivity(new Intent(this, FavoriteActivity.class)));

        ImageView profileIcon = findViewById(R.id.profile_icon);
        profileIcon.setOnClickListener(v -> startActivity(new Intent(this, ProfilActivity.class)));

        // Tombol tambah restoran
        findViewById(R.id.fabAddFood).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InsertFoodActivity.class);
            startActivity(intent);
        });
    }
}
