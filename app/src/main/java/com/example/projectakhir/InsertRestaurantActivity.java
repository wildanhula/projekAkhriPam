package com.example.projectakhir;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class InsertRestaurantActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_PERMISSIONS = 100;

    private EditText editName, editDesc, editLocation, editPrice, editOpeningHours;
    private ImageView imageViewPreview;
    private Uri imageUri = null;
    private Bitmap imageBitmap = null;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_restaurant);

        editName = findViewById(R.id.editName);
        editDesc = findViewById(R.id.editDesc);
        editLocation = findViewById(R.id.editLocation);
        editPrice = findViewById(R.id.editPrice);
        editOpeningHours = findViewById(R.id.editOpeningHours);
        imageViewPreview = findViewById(R.id.imageViewPreview);
        Button btnPickImage = findViewById(R.id.btnPickImage);
        Button btnSave = findViewById(R.id.btnSave);

        checkAndRequestPermissions();

        databaseReference = FirebaseDatabase.getInstance(
                "https://projectakhir-d24d3-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference("restaurants");

        btnPickImage.setOnClickListener(v -> showImagePickerDialog());

        btnSave.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String desc = editDesc.getText().toString().trim();
            String location = editLocation.getText().toString().trim();
            String price = editPrice.getText().toString().trim();
            String openingHours = editOpeningHours.getText().toString().trim();

            if (name.isEmpty() || desc.isEmpty() || location.isEmpty() || price.isEmpty() || openingHours.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            String imageUriString = (imageUri != null) ? imageUri.toString() : "";
            saveRestaurant(name, desc, location, price, openingHours, imageUriString);
        });
    }

    private void checkAndRequestPermissions() {
        List<String> permissionList = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.READ_MEDIA_IMAGES);
            }
        }

        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionList.toArray(new String[0]),
                    REQUEST_PERMISSIONS);
        }
    }

    private void showImagePickerDialog() {
        String[] options = {"Ambil dari Kamera", "Pilih dari Galeri"};
        new AlertDialog.Builder(this)
                .setTitle("Pilih Gambar")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                        }
                    } else {
                        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickIntent, REQUEST_IMAGE_PICK);
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                imageUri = data.getData();
                imageBitmap = null;
                imageViewPreview.setImageURI(imageUri);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && data != null && data.getExtras() != null) {
                imageBitmap = (Bitmap) data.getExtras().get("data");
                imageUri = null;
                imageViewPreview.setImageBitmap(imageBitmap);
            }
        }
    }

    private void saveRestaurant(String name, String desc, String location, String price, String openingHours, String imageUrl) {
        String id = databaseReference.push().getKey();
        if (id != null) {
            Restaurant restaurant = new Restaurant(id, name, desc, location, price, openingHours, imageUrl);
            databaseReference.child(id).setValue(restaurant)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Restoran ditambahkan", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Izin dibutuhkan untuk kamera & galeri", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }
}
