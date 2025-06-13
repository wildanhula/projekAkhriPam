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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewActivity extends AppCompatActivity implements ReviewAdapter.OnReviewClickListener {

    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int REQUEST_PERMISSION = 4;

    private EditText etMenu, etReview;
    private Button btnAddImage, btnSendReview;
    private ImageView ivPreview;
    private RecyclerView reviewRecyclerView;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;
    private String selectedImagePath = null;

    private DatabaseReference reviewDatabase;

    // List untuk menyimpan review yang di-bookmark
    private List<Review> bookmarkedReviews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        reviewDatabase = FirebaseDatabase.getInstance("https://projectakhir-d24d3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("reviews");

        etMenu = findViewById(R.id.etMenu);
        etReview = findViewById(R.id.etReview);
        btnAddImage = findViewById(R.id.btnAddImage);
        btnSendReview = findViewById(R.id.btnSendReview);
        ivPreview = findViewById(R.id.ivPreview);

        reviewRecyclerView = findViewById(R.id.reviewRecyclerView);
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviewList, this);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewRecyclerView.setAdapter(reviewAdapter);

        btnAddImage.setOnClickListener(v -> showImagePickerDialog());
        btnSendReview.setOnClickListener(v -> submitReview());

        requestPermissions();
    }

    private void requestPermissions() {
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        List<String> requestList = new ArrayList<>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                requestList.add(perm);
            }
        }

        if (!requestList.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    requestList.toArray(new String[0]), REQUEST_PERMISSION);
        }
    }

    private void showImagePickerDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Pilih Gambar")
                .setItems(new String[]{"Kamera", "Galeri"}, (dialog, which) -> {
                    if (which == 0) openCamera();
                    else openGallery();
                }).show();
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void openGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_IMAGE_PICK);
        }
    }

    private String saveImageToInternalStorage(Bitmap bitmap) {
        try {
            String fileName = "review_" + System.currentTimeMillis() + ".jpg";
            File file = new File(getFilesDir(), fileName);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void submitReview() {
        String menu = etMenu.getText().toString().trim();
        String reviewText = etReview.getText().toString().trim();

        if (menu.isEmpty() || reviewText.isEmpty()) {
            Toast.makeText(this, "Menu dan Review tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String id = reviewDatabase.push().getKey();

        Review review = new Review(
                id,
                currentDate,
                menu,
                reviewText,
                menu,
                selectedImagePath,
                selectedImagePath != null ? 0 : R.drawable.restoran
        );

        if (id != null) {
            reviewDatabase.child(id).setValue(review).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    reviewList.add(0, review);
                    reviewAdapter.notifyItemInserted(0);
                    reviewRecyclerView.smoothScrollToPosition(0);
                    Toast.makeText(this, "Review berhasil disimpan", Toast.LENGTH_SHORT).show();

                    etMenu.setText("");
                    etReview.setText("");
                    ivPreview.setImageBitmap(null);
                    ivPreview.setVisibility(View.GONE);
                    selectedImagePath = null;
                } else {
                    Toast.makeText(this, "Gagal menyimpan ke database", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = null;

            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    bitmap = (Bitmap) extras.get("data");
                }
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    try (InputStream stream = getContentResolver().openInputStream(imageUri)) {
                        bitmap = BitmapFactory.decodeStream(stream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (bitmap != null) {
                selectedImagePath = saveImageToInternalStorage(bitmap);
                if (selectedImagePath != null) {
                    ivPreview.setImageBitmap(bitmap);
                    ivPreview.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    // Callback kosong dari interface (sudah ada)
    @Override public void onReviewClick(Review review, int position) {}
    @Override public void onDeleteClick(Review review, int position) {}
    @Override public void onReviewUpdated(Review review, int position) {}

    // IMPLEMENTASI BARU - Method untuk menangani tombol Like, Share, dan Bookmark
    @Override
    public void onLikeClick(Review review, int position) {
        // Toggle status like
        review.setLiked(!review.isLiked());

        // Update like count
        if (review.isLiked()) {
            review.setLikeCount(review.getLikeCount() + 1);
            Toast.makeText(this, "Review disukai!", Toast.LENGTH_SHORT).show();
        } else {
            review.setLikeCount(Math.max(0, review.getLikeCount() - 1));
            Toast.makeText(this, "Like dibatalkan", Toast.LENGTH_SHORT).show();
        }

        // Update tampilan
        reviewAdapter.notifyItemChanged(position);

        // Update ke Firebase (opsional)
        if (review.getId() != null) {
            reviewDatabase.child(review.getId()).child("liked").setValue(review.isLiked());
            reviewDatabase.child(review.getId()).child("likeCount").setValue(review.getLikeCount());
        }
    }

    @Override
    public void onShareClick(Review review, int position) {
        // Buat teks untuk dibagikan
        String shareText = "Review Makanan:\n\n" +
                "Menu: " + review.getMenu() + "\n" +
                "Review: " + review.getReview() + "\n" +
                "Tanggal: " + review.getDate() + "\n\n" +
                "Dibagikan dari Aplikasi Review Makanan";

        // Intent untuk share
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Review: " + review.getMenu());

        // Tampilkan chooser
        Intent chooser = Intent.createChooser(shareIntent, "Bagikan Review via:");
        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
            Toast.makeText(this, "Membagikan review...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Tidak ada aplikasi untuk berbagi", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBookmarkClick(Review review, int position) {
        // Toggle status bookmark
        review.setBookmarked(!review.isBookmarked());

        if (review.isBookmarked()) {
            // Tambah ke bookmark list jika belum ada
            if (!bookmarkedReviews.contains(review)) {
                bookmarkedReviews.add(review);
            }
            Toast.makeText(this, "Review disimpan ke bookmark!", Toast.LENGTH_SHORT).show();
        } else {
            // Hapus dari bookmark list
            bookmarkedReviews.remove(review);
            Toast.makeText(this, "Review dihapus dari bookmark", Toast.LENGTH_SHORT).show();
        }

        // Update tampilan
        reviewAdapter.notifyItemChanged(position);

        // Update ke Firebase (opsional)
        if (review.getId() != null) {
            reviewDatabase.child(review.getId()).child("bookmarked").setValue(review.isBookmarked());
        }
    }

    // Method tambahan untuk mendapatkan daftar bookmark
    public List<Review> getBookmarkedReviews() {
        return new ArrayList<>(bookmarkedReviews);
    }

    // Method untuk menampilkan hanya review yang di-bookmark
    public void showBookmarkedReviews() {
        if (bookmarkedReviews.isEmpty()) {
            Toast.makeText(this, "Belum ada review yang di-bookmark", Toast.LENGTH_SHORT).show();
        } else {
            // Implementasi untuk menampilkan bookmark bisa disesuaikan
            // Misalnya buka activity baru atau filter RecyclerView
            Toast.makeText(this, "Anda memiliki " + bookmarkedReviews.size() + " review di bookmark", Toast.LENGTH_LONG).show();
        }
    }
}