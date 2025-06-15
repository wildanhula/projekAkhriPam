package com.example.projectakhir;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.UUID;

public class EditRestaurantActivity extends AppCompatActivity {

    private EditText editName, editDesc, editLocation, editPrice, editHours;
    private ImageView imageViewPreview;
    private Button btnPickImage, btnSave, btnDelete;

    private Uri imageUri;
    private String imageUrl;
    private String restaurantId;

    private DatabaseReference databaseRef;
    private StorageReference storageRef;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_restaurant);

        // Inisialisasi komponen
        editName = findViewById(R.id.editName);
        editDesc = findViewById(R.id.editDesc);
        editLocation = findViewById(R.id.editLocation);
        editPrice = findViewById(R.id.editPrice);
        editHours = findViewById(R.id.editOpeningHours);
        imageViewPreview = findViewById(R.id.imageViewPreview);
        btnPickImage = findViewById(R.id.btnPickImage);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDeleteFood);

        // Inisialisasi Firebase
        databaseRef = FirebaseDatabase.getInstance(
                        "https://projectakhir-d24d3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("restaurants");
        storageRef = FirebaseStorage.getInstance().getReference("restaurant_images");

        // Ambil data dari Intent
        Intent intent = getIntent();
        restaurantId = intent.getStringExtra("restaurantId");
        editName.setText(intent.getStringExtra("restaurantName"));
        editDesc.setText(intent.getStringExtra("restaurantDesc"));
        editLocation.setText(intent.getStringExtra("restaurantLocation"));
        editPrice.setText(intent.getStringExtra("restaurantPrice"));
        editHours.setText(intent.getStringExtra("restaurantHours"));
        imageUrl = intent.getStringExtra("restaurantImageUrl");

        // Tampilkan gambar awal
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).into(imageViewPreview);
        }

        btnPickImage.setOnClickListener(v -> pickImage());
        btnSave.setOnClickListener(v -> updateRestaurant());
        btnDelete.setOnClickListener(v -> deleteRestaurant());
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageViewPreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal menampilkan gambar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateRestaurant() {
        String name = editName.getText().toString().trim();
        String desc = editDesc.getText().toString().trim();
        String location = editLocation.getText().toString().trim();
        String price = editPrice.getText().toString().trim();
        String hours = editHours.getText().toString().trim();

        if (name.isEmpty() || desc.isEmpty() || location.isEmpty() || price.isEmpty() || hours.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Mengunggah gambar baru...");
            progressDialog.show();

            StorageReference fileRef = storageRef.child(UUID.randomUUID().toString());
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        progressDialog.dismiss();
                        saveToDatabase(name, desc, location, price, hours, uri.toString());
                    })
            ).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(this, "Gagal upload gambar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            saveToDatabase(name, desc, location, price, hours, imageUrl);
        }
    }

    private void saveToDatabase(String name, String desc, String location,
                                String price, String hours, String imageUrl) {
        RestaurantModel restaurant = new RestaurantModel(restaurantId, imageUrl, name, desc, location, price, hours);

        databaseRef.child(restaurantId).setValue(restaurant).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Gagal memperbarui data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteRestaurant() {
        databaseRef.child(restaurantId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Gagal menghapus data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
