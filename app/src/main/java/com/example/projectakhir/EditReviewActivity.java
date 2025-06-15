package com.example.projectakhir;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditReviewActivity extends AppCompatActivity {

    private static final int REQUEST_GALLERY = 101;
    private static final int REQUEST_CAMERA = 102;

    private TextInputEditText etReview;
    private ImageView ivImagePreview;
    private Button btnCaptureImage, btnDelete, btnSubmit;

    private Review review;
    private DatabaseReference reviewRef;
    private FirebaseUser currentUser;

    private Bitmap selectedBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_review);

        etReview = findViewById(R.id.et_isi);
        ivImagePreview = findViewById(R.id.image_preview);
        btnCaptureImage = findViewById(R.id.btn_capture_image);
        btnDelete = findViewById(R.id.btn_delete);
        btnSubmit = findViewById(R.id.btn_submit);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User belum login", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        review = getIntent().getParcelableExtra("review");
        if (review == null) {
            Toast.makeText(this, "Data review tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        reviewRef = FirebaseDatabase.getInstance("https://projectakhir-d24d3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users").child(currentUser.getUid()).child("reviews");

        etReview.setText(review.getReview());

        // Tampilkan gambar jika ada Base64
        if (review.getImagePath() != null && review.getImagePath().length() > 100) {
            byte[] imageBytes = Base64.decode(review.getImagePath(), Base64.DEFAULT);
            selectedBitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            ivImagePreview.setImageBitmap(selectedBitmap);
        }

        btnCaptureImage.setOnClickListener(v -> showImageSourceDialog());

        btnSubmit.setOnClickListener(v -> {
            String isi = etReview.getText().toString().trim();
            if (isi.isEmpty()) {
                Toast.makeText(this, "Isi review tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }

            review.setReview(isi);
            review.setDate(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date()));

            if (selectedBitmap != null) {
                review.setImagePath(encodeImageToBase64(selectedBitmap));
            }

            reviewRef.child(review.getId()).setValue(review).addOnSuccessListener(unused -> {
                Toast.makeText(this, "Review berhasil diupdate", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }).addOnFailureListener(e -> Toast.makeText(this, "Gagal update review", Toast.LENGTH_SHORT).show());
        });

        btnDelete.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Hapus Review")
                .setMessage("Yakin ingin menghapus review ini?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    reviewRef.child(review.getId()).removeValue().addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Review dihapus", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    });
                })
                .setNegativeButton("Batal", null)
                .show());
    }

    private void showImageSourceDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Pilih Sumber Gambar")
                .setItems(new String[]{"Dari Kamera", "Dari Galeri"}, (dialog, which) -> {
                    if (which == 0) openCamera();
                    else openGallery();
                }).show();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQUEST_GALLERY) {
                Uri imageUri = data.getData();
                try {
                    selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    ivImagePreview.setImageBitmap(selectedBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Gagal mengambil gambar", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == REQUEST_CAMERA) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    selectedBitmap = (Bitmap) extras.get("data");
                    ivImagePreview.setImageBitmap(selectedBitmap);
                }
            }
        }
    }
}
