package com.example.projectakhir;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBottom;
    private RestaurantAdapter adapterBottom;
    private List<Restaurant> restaurantList;
    private DatabaseReference databaseReference;
    private FloatingActionButton fabAddRestaurant;
    private ImageView imgHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        // Inisialisasi view
        recyclerViewBottom = findViewById(R.id.recyclerViewBottom);
        fabAddRestaurant = findViewById(R.id.fabAddRestaurant);
        imgHome = findViewById(R.id.imgHome);

        // Setup RecyclerView
        restaurantList = new ArrayList<>();
        adapterBottom = new RestaurantAdapter(this, restaurantList);
        recyclerViewBottom.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewBottom.setAdapter(adapterBottom);

        // Ambil data dari Firebase
        databaseReference = FirebaseDatabase.getInstance(
                "https://projectakhir-d24d3-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference("restaurants");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                restaurantList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Restaurant restaurant = data.getValue(Restaurant.class);
                    if (restaurant != null) {
                        restaurantList.add(restaurant);
                    }
                }
                adapterBottom.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Error dari Firebase
                // Bisa tambahkan log atau Toast jika ingin
            }
        });

        // Navigasi ke MainActivity saat tombol Home ditekan
        imgHome.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // opsional, supaya tidak bisa kembali ke FavoriteActivity dengan tombol back
        });

        // Navigasi ke InsertRestaurantActivity saat FAB ditekan
        fabAddRestaurant.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteActivity.this, InsertRestaurantActivity.class);
            startActivity(intent);
        });
    }
}
