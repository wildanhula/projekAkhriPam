// EditReviewActivity.java - Final version with camera capture support
package com.example.projectakhir;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditReviewActivity extends AppCompatActivity {

    private TextInputEditText etReview;
    private ImageView ivImagePreview;
    private Button btnCaptureImage, btnDelete, btnSubmit;

    private Review review;
    private DatabaseReference reviewRef;

    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_IMAGE_PICK = 102;
    private static final int REQUEST_PERMISSION = 103;

    private String imagePathToSave = null;
    private Uri cameraImageUri;
    private boolean imageChanged = false;
    private boolean hasImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_review);

        initializeViews();
        initializeFirebase();
        loadReviewData();
        setupClickListeners();
        requestPermissions();
    }

    private void initializeViews() {
        etReview = findViewById(R.id.et_isi);
        ivImagePreview = findViewById(R.id.image_preview);
        btnCaptureImage = findViewById(R.id.btn_capture_image);
        btnDelete = findViewById(R.id.btn_delete);
        btnSubmit = findViewById(R.id.btn_submit);
    }

    private void initializeFirebase() {
        reviewRef = FirebaseDatabase.getInstance("https://projectakhir-d24d3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("reviews");
    }

    private void loadReviewData() {
        review = getIntent().getParcelableExtra("review");
        if (review != null) {
            etReview.setText(review.getReview());
            imagePathToSave = review.getImagePath();
            loadImagePreview();
            updateButtonText();
        }
    }

    private void loadImagePreview() {
        if (imagePathToSave != null && !imagePathToSave.isEmpty()) {
            File file = new File(imagePathToSave);
            if (file.exists()) {
                Glide.with(this)
                        .load(file)
                        .placeholder(R.drawable.restoran)
                        .into(ivImagePreview);
                hasImage = true;
                ivImagePreview.setVisibility(View.VISIBLE);
            } else {
                setDefaultImage();
            }
        } else {
            setDefaultImage();
        }
    }

    private void setDefaultImage() {
        ivImagePreview.setImageResource(R.drawable.restoran);
        ivImagePreview.setVisibility(View.VISIBLE);
        hasImage = false;
    }

    private void updateButtonText() {
        btnCaptureImage.setText(hasImage ? "Ganti/Hapus Gambar" : "Ambil Gambar dari Kamera");
    }

    private void setupClickListeners() {
        btnCaptureImage.setOnClickListener(v -> {
            if (hasImage) {
                showImageOptionsDialog();
            } else {
                showImagePickerDialog();
            }
        });

        btnSubmit.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Simpan Perubahan")
                .setMessage("Apakah Anda yakin ingin menyimpan perubahan ini?")
                .setPositiveButton("Ya", (dialog, which) -> updateReview())
                .setNegativeButton("Batal", null)
                .show());

        btnDelete.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Hapus Review")
                .setMessage("Apakah Anda yakin ingin menghapus review ini?")
                .setPositiveButton("Ya", (dialog, which) -> deleteReview())
                .setNegativeButton("Batal", null)
                .show());
    }

    private void showImageOptionsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Pilih Aksi")
                .setItems(new String[]{"Ganti Gambar", "Hapus Gambar"}, (dialog, which) -> {
                    if (which == 0) showImagePickerDialog();
                    else removeImage();
                }).show();
    }

    private void showImagePickerDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Pilih Sumber Gambar")
                .setItems(new String[]{"Kamera", "Galeri"}, (dialog, which) -> {
                    if (which == 0) openCamera();
                    else openGallery();
                }).show();
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION);
            return;
        }

        File imageFile = createImageFile();
        if (imageFile != null) {
            cameraImageUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", imageFile);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private File createImageFile() {
        try {
            String fileName = "review_edited_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File file = new File(getFilesDir(), fileName + ".jpg");
            imagePathToSave = file.getAbsolutePath();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void openGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            return;
        }
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_IMAGE_PICK);
    }

    private void removeImage() {
        if (imagePathToSave != null && !imagePathToSave.isEmpty()) {
            File oldFile = new File(imagePathToSave);
            if (oldFile.exists()) oldFile.delete();
        }
        imagePathToSave = null;
        imageChanged = true;
        hasImage = false;
        setDefaultImage();
        updateButtonText();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && imagePathToSave != null) {
                File file = new File(imagePathToSave);
                if (file.exists()) {
                    imageChanged = true;
                    hasImage = true;
                    Glide.with(this).load(file).placeholder(R.drawable.restoran).into(ivImagePreview);
                    updateButtonText();
                }
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                Uri imageUri = data.getData();
                try (InputStream stream = getContentResolver().openInputStream(imageUri)) {
                    Bitmap bitmap = BitmapFactory.decodeStream(stream);
                    if (bitmap != null) {
                        if (imagePathToSave != null) {
                            File old = new File(imagePathToSave);
                            if (old.exists()) old.delete();
                        }
                        imagePathToSave = saveImageToInternalStorage(bitmap);
                        imageChanged = true;
                        hasImage = true;
                        Glide.with(this).load(new File(imagePathToSave)).into(ivImagePreview);
                        updateButtonText();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String saveImageToInternalStorage(Bitmap bitmap) {
        try {
            File file = new File(getFilesDir(), "review_edited_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void updateReview() {
        String updatedText = etReview.getText().toString().trim();
        if (updatedText.isEmpty()) {
            Toast.makeText(this, "Isi review tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        review.setReview(updatedText);
        review.setImagePath(imagePathToSave);

        if (review.getId() != null) {
            reviewRef.child(review.getId()).setValue(review).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updated_review", review);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(this, "Gagal memperbarui review", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void deleteReview() {
        if (review.getId() != null) {
            if (review.getImagePath() != null) {
                File file = new File(review.getImagePath());
                if (file.exists()) file.delete();
            }
            reviewRef.child(review.getId()).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("deleted_review_id", review.getId());
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(this, "Gagal menghapus review", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void requestPermissions() {
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (!allGranted) {
                Toast.makeText(this, "Izin diperlukan untuk menggunakan fitur gambar", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        String currentText = etReview.getText().toString().trim();
        if (!currentText.equals(review.getReview()) || imageChanged) {
            new AlertDialog.Builder(this)
                    .setTitle("Perubahan Belum Disimpan")
                    .setMessage("Anda memiliki perubahan yang belum disimpan. Apakah Anda yakin ingin keluar?")
                    .setPositiveButton("Ya", (dialog, which) -> super.onBackPressed())
                    .setNegativeButton("Batal", null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }
}