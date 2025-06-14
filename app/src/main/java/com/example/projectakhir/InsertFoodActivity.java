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

public class InsertFoodActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editName, editDesc, editLocation, editPrice, editOpeningHours;
    private ImageView imgFood;
    private Button btnPickImage, btnSave;
    private Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_food);

        editName = findViewById(R.id.editFoodName);
        editDesc = findViewById(R.id.editFoodDesc);
        editLocation = findViewById(R.id.editFoodLocation);
        editPrice = findViewById(R.id.editFoodPrice);
        editOpeningHours = findViewById(R.id.editOpeningHours);
        imgFood = findViewById(R.id.imgFood);
        btnPickImage = findViewById(R.id.btnPickImage);
        btnSave = findViewById(R.id.btnSaveFood);

        btnPickImage.setOnClickListener(v -> openImageChooser());
        btnSave.setOnClickListener(v -> saveFood());
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
            Glide.with(this).load(imageUri).into(imgFood);
        }
    }

    private void saveFood() {
        String title = editName.getText().toString().trim();
        String description = editDesc.getText().toString().trim();
        String location = editLocation.getText().toString().trim();
        String price = editPrice.getText().toString().trim();
        String openingHours = editOpeningHours.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || imageUri == null ||
                location.isEmpty() || price.isEmpty() || openingHours.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi dan gambar dipilih!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference foodsRef = FirebaseDatabase.getInstance(
                "https://projectakhir-d24d3-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference("foods");

        String id = foodsRef.push().getKey();
        FoodModel food = new FoodModel(id, imageUri.toString(), title, description, location, price, openingHours);

        foodsRef.child(id).setValue(food)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Makanan berhasil disimpan", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal menyimpan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
