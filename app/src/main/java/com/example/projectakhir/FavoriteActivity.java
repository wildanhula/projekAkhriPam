package com.example.projectakhir;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBottom;
    private RestaurantAdapter adapterBottom;
    private List<Restaurant> restaurantList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity); // Changed from main_activity to activity_favorite

        // Initialize views
        recyclerViewBottom = findViewById(R.id.recyclerViewBottom);
        ImageView imgHome = findViewById(R.id.imgHome); // Corrected variable name

        // Initialize restaurant list
        restaurantList = new ArrayList<>();
        restaurantList.add(new Restaurant("Eat Restaurant", "Great food and place", R.drawable.restoran, 4.5f));
        restaurantList.add(new Restaurant("Sushi World", "Fresh sushi all day", R.drawable.restoran, 4.0f));
        restaurantList.add(new Restaurant("Burger Town", "Best burgers in town", R.drawable.restoran, 4.2f));
        restaurantList.add(new Restaurant("Pizza Palace", "Cheesy and delicious", R.drawable.restoran, 4.6f));
        restaurantList.add(new Restaurant("Noodle House", "Authentic Asian noodles", R.drawable.restoran, 4.3f));
        restaurantList.add(new Restaurant("Grill & BBQ", "Smoky flavors, juicy meats", R.drawable.restoran, 4.4f));
        restaurantList.add(new Restaurant("Vegan Delight", "Fresh and healthy meals", R.drawable.restoran, 4.1f));
        restaurantList.add(new Restaurant("Taco Fiesta", "Spicy and full of flavor", R.drawable.restoran, 4.2f));
        restaurantList.add(new Restaurant("Coffee Corner", "Cozy place for coffee lovers", R.drawable.restoran, 4.7f));
        restaurantList.add(new Restaurant("Seafood Shack", "Fresh from the ocean", R.drawable.restoran, 4.3f));

        // Setup RecyclerView
        adapterBottom = new RestaurantAdapter(this, restaurantList);
        recyclerViewBottom.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewBottom.setAdapter(adapterBottom);

        // Set click listener for home button
        imgHome.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}