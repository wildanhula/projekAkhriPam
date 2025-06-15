package com.example.projectakhir;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReviewActivity extends AppCompatActivity implements ReviewAdapter.OnReviewClickListener {

    private static final int REQUEST_IMAGE_PICK = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 102;
    private static final int REQUEST_PERMISSION = 103;

    private EditText etMenu, etReview;
    private Button btnAddImage, btnSendReview;
    private ImageView ivPreview, imageRestaurant;
    private TextView tvRestaurantName, tvLocation, tvOpeningHours, tvPrice;
    private RecyclerView reviewRecyclerView;

    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList = new ArrayList<>();

    private String selectedImageBase64 = null;
    private String selectedMenuTitle = "";
    private FirebaseAuth mAuth;
    private DatabaseReference reviewDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        reviewDatabase = FirebaseDatabase.getInstance("https://projectakhir-d24d3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users").child(user.getUid()).child("reviews");

        // Ambil data dari intent
        selectedMenuTitle = getIntent().getStringExtra("title");
        String imageUri = getIntent().getStringExtra("imageUri");
        String location = getIntent().getStringExtra("location");
        String openingHours = getIntent().getStringExtra("openingHours");
        String price = getIntent().getStringExtra("price");

        // View binding
        etMenu = findViewById(R.id.etMenu);
        etReview = findViewById(R.id.etReview);
        btnAddImage = findViewById(R.id.btnAddImage);
        btnSendReview = findViewById(R.id.btnSendReview);
        ivPreview = findViewById(R.id.ivPreview);
        imageRestaurant = findViewById(R.id.imageRestaurant);
        tvRestaurantName = findViewById(R.id.tvRestaurantName);
        tvLocation = findViewById(R.id.tvLocation);
        tvOpeningHours = findViewById(R.id.tvOpeningHours);
        tvPrice = findViewById(R.id.tvPrice);

        tvRestaurantName.setText(selectedMenuTitle);
        tvLocation.setText(location);
        tvOpeningHours.setText(openingHours);
        tvPrice.setText(price);
        Glide.with(this).load(imageUri).placeholder(R.drawable.restoran).into(imageRestaurant);

        etMenu.setText(selectedMenuTitle);
        etMenu.setVisibility(View.GONE);

        reviewRecyclerView = findViewById(R.id.reviewRecyclerView);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(reviewList, this);
        reviewRecyclerView.setAdapter(reviewAdapter);

        btnAddImage.setOnClickListener(v -> showImagePickerDialog());
        btnSendReview.setOnClickListener(v -> submitReview());

        setupBottomNavigation();
        requestPermissions();
        loadReviewsFromFirebase();
    }

    private void showImagePickerDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Pilih Sumber Gambar")
                .setItems(new String[]{"Kamera", "Galeri"}, (dialog, which) -> {
                    if (which == 0) openCamera();
                    else openGallery();
                }).show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap bitmap = null;

            if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                Uri imageUri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bundle extras = data.getExtras();
                bitmap = (Bitmap) extras.get("data");
            }

            if (bitmap != null) {
                selectedImageBase64 = encodeBitmapToBase64(bitmap);
                ivPreview.setImageBitmap(bitmap);
                ivPreview.setVisibility(View.VISIBLE);
            }
        }
    }

    private String encodeBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
        byte[] byteArray = baos.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void submitReview() {
        String reviewText = etReview.getText().toString().trim();
        if (reviewText.isEmpty()) {
            Toast.makeText(this, "Review tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String id = reviewDatabase.push().getKey();

        Review review = new Review(
                id,
                date,
                selectedMenuTitle,
                reviewText,
                selectedMenuTitle,
                selectedImageBase64,
                selectedImageBase64 != null ? 0 : R.drawable.restoran
        );

        if (id != null) {
            reviewDatabase.child(id).setValue(review).addOnSuccessListener(unused -> {
                Toast.makeText(this, "Review berhasil dikirim", Toast.LENGTH_SHORT).show();
                etReview.setText("");
                ivPreview.setVisibility(View.GONE);
                selectedImageBase64 = null;
            });
        }
    }

    private void loadReviewsFromFirebase() {
        reviewDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Review review = dataSnapshot.getValue(Review.class);
                    if (review != null && selectedMenuTitle.equalsIgnoreCase(review.getMenu())) {
                        reviewList.add(0, review);
                    }
                }
                reviewAdapter.updateData(reviewList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReviewActivity.this, "Gagal memuat review", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestPermissions() {
        List<String> permissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA);
        }

        if (!permissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), REQUEST_PERMISSION);
        }
    }

    private void setupBottomNavigation() {
        ImageView homeIcon = findViewById(R.id.home_icon);
        ImageView notificationIcon = findViewById(R.id.notification_icon);
        ImageView journalIcon = findViewById(R.id.journal_icon);
        ImageView saveIcon = findViewById(R.id.imgSave);
        ImageView profileIcon = findViewById(R.id.profile_icon);

        homeIcon.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        notificationIcon.setOnClickListener(v -> Toast.makeText(this, "Fitur belum tersedia", Toast.LENGTH_SHORT).show());
        journalIcon.setOnClickListener(v -> startActivity(new Intent(this, JournalActivity.class)));
        saveIcon.setOnClickListener(v -> startActivity(new Intent(this, FavoriteActivity.class)));
        profileIcon.setOnClickListener(v -> startActivity(new Intent(this, ProfilActivity.class)));
    }

    // Interface methods (kosongkan jika belum digunakan)
    @Override public void onReviewClick(Review review, int position) {}
    @Override public void onDeleteClick(Review review, int position) {}
    @Override public void onReviewUpdated(Review review, int position) {}
    @Override public void onLikeClick(Review review, int position) {}
    @Override public void onShareClick(Review review, int position) {}
    @Override public void onBookmarkClick(Review review, int position) {}
}
