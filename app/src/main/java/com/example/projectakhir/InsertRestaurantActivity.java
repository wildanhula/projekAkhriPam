package com.example.projectakhir;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InsertRestaurantActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editName, editDesc, editLocation, editPrice, editOpeningHours;
    private ImageView imageViewPreview;
    private Button btnPickImage, btnSave;
    private Uri imageUri;

    private boolean isEdit = false;
    private String restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_restaurant);

        // Inisialisasi view
        editName = findViewById(R.id.editName);
        editDesc = findViewById(R.id.editDesc);
        editLocation = findViewById(R.id.editLocation);
        editPrice = findViewById(R.id.editPrice);
        editOpeningHours = findViewById(R.id.editOpeningHours);
        imageViewPreview = findViewById(R.id.imageViewPreview);
        btnPickImage = findViewById(R.id.btnPickImage);
        btnSave = findViewById(R.id.btnSave);

        // Cek apakah edit
        if (getIntent().hasExtra("restaurant")) {
            isEdit = true;
            RestaurantModel restaurant = (RestaurantModel) getIntent().getSerializableExtra("restaurant");
            restaurantId = restaurant.getId();

            editName.setText(restaurant.getName());
            editDesc.setText(restaurant.getDescription());
            editLocation.setText(restaurant.getLocation());
            editPrice.setText(restaurant.getPrice());
            editOpeningHours.setText(restaurant.getOpeningHours());

            try {
                if (restaurant.getImageUrl() != null && !restaurant.getImageUrl().equals("null")) {
                    imageUri = Uri.parse(restaurant.getImageUrl());
                    Glide.with(this)
                            .load(imageUri)
                            .placeholder(R.drawable.restoran)
                            .error(R.drawable.restoran)
                            .into(imageViewPreview);
                }
            } catch (Exception e) {
                Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show();
            }
        }

        btnPickImage.setOnClickListener(v -> openImageChooser());
        btnSave.setOnClickListener(v -> saveOrUpdateRestaurant());
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(imageViewPreview);
        }
    }

    private void saveOrUpdateRestaurant() {
        String name = editName.getText().toString().trim();
        String desc = editDesc.getText().toString().trim();
        String location = editLocation.getText().toString().trim();
        String price = editPrice.getText().toString().trim();
        String openingHours = editOpeningHours.getText().toString().trim();

        if (name.isEmpty() || desc.isEmpty() || location.isEmpty() ||
                price.isEmpty() || openingHours.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Semua field dan gambar wajib diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference dbRef = FirebaseDatabase.getInstance(
                        "https://projectakhir-d24d3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("restaurants");

        String id = isEdit ? restaurantId : dbRef.push().getKey();

        RestaurantModel restaurant = new RestaurantModel(
                id, imageUri.toString(), name, desc, location, price, openingHours
        );

        dbRef.child(id).setValue(restaurant)
                .addOnSuccessListener( task -> {
                    Toast.makeText(this,
                            isEdit ? "Restoran berhasil diperbarui" : "Restoran berhasil disimpan",
                            Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal menyimpan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}