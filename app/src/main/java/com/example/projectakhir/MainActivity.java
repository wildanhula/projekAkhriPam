package com.example.projectakhir;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi RecyclerView
        RecyclerView recyclerExploreFood = findViewById(R.id.recyclerExploreFood);
        RecyclerView recyclerMostlyVisited = findViewById(R.id.recyclerMostlyVisited);

        // Mengatur layout manager agar bisa scroll horizontal
        recyclerExploreFood.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerMostlyVisited.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Data untuk RecyclerView
        List<FoodModel> foodList = Arrays.asList(
                new FoodModel(R.drawable.restoran, "Spill Pasta", "Pasta dengan berbagai saus spesial"),
                new FoodModel(R.drawable.restoran, "Resto Jepang", "Aneka sushi dan ramen khas Jepang"),
                new FoodModel(R.drawable.restoran, "Pizza Hut", "Pizza lezat dengan berbagai topping"),
                new FoodModel(R.drawable.restoran, "Bakso Malang", "Bakso kuah khas Malang dengan pangsit"),
                new FoodModel(R.drawable.restoran, "Ayam Bakar", "Ayam bakar dengan bumbu rempah"),
                new FoodModel(R.drawable.restoran, "Sate Madura", "Sate ayam khas Madura dengan bumbu kacang")
        );

        // Menggunakan adapter untuk menampilkan data
        FoodAdapter adapter = new FoodAdapter(this, foodList);
        recyclerExploreFood.setAdapter(adapter);
        recyclerMostlyVisited.setAdapter(adapter);
    }
}
