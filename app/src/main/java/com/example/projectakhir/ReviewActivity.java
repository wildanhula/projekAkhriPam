// ReviewActivity.java (final version with edit/delete support)
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReviewActivity extends AppCompatActivity implements ReviewAdapter.OnReviewClickListener {

    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int REQUEST_PERMISSION = 4;
    private static final int REQUEST_EDIT_REVIEW = 200;

    private EditText etMenu, etReview;
    private Button btnAddImage, btnSendReview;
    private ImageView ivPreview, imageRestaurant;
    private TextView tvRestaurantName, tvLocation, tvOpeningHours, tvPrice;
    private RecyclerView reviewRecyclerView;

    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList = new ArrayList<>();
    private List<Review> bookmarkedReviews = new ArrayList<>();

    private String selectedImagePath = null;
    private String selectedMenuTitle = "";

    private DatabaseReference reviewDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);


        ImageView homeIcon = findViewById(R.id.home_icon);
        ImageView notificationIcon = findViewById(R.id.notification_icon);
        ImageView journalIcon = findViewById(R.id.journal_icon);
        ImageView saveIcon = findViewById(R.id.imgSave);
        ImageView profileIcon = findViewById(R.id.profile_icon);

        homeIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ReviewActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // optional, if you don't want user to go back
        });

//        notificationIcon.setOnClickListener(v -> {
//            Intent intent = new Intent(ReviewActivity.this, NotificationActivity.class);
//            startActivity(intent);
//            Toast.makeText(this, "Fitur belum tersedia", Toast.LENGTH_SHORT).show();
//
//        });

        journalIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ReviewActivity.this, JournalActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        saveIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ReviewActivity.this, FavoriteActivity.class);
            startActivity(intent);
            finish();
        });
        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ReviewActivity.this, ProfilActivity.class);
            startActivity(intent);
        });





        selectedMenuTitle = getIntent().getStringExtra("title");
        String imageUri = getIntent().getStringExtra("imageUri");
        String location = getIntent().getStringExtra("location");
        String openingHours = getIntent().getStringExtra("openingHours");
        String price = getIntent().getStringExtra("price");

        reviewDatabase = FirebaseDatabase.getInstance("https://projectakhir-d24d3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("reviews");













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

        loadReviewsFromFirebase();

        btnAddImage.setOnClickListener(v -> showImagePickerDialog());
        btnSendReview.setOnClickListener(v -> submitReview());

        requestPermissions();
    }

    private void loadReviewsFromFirebase() {
        reviewDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Review review = dataSnapshot.getValue(Review.class);
                    if (review != null && selectedMenuTitle.equalsIgnoreCase(review.getMenu())) {
                        reviewList.add(review);
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

    private void submitReview() {
        String reviewText = etReview.getText().toString().trim();
        if (reviewText.isEmpty()) {
            Toast.makeText(this, "Review tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String id = reviewDatabase.push().getKey();

        Review review = new Review(
                id,
                currentDate,
                selectedMenuTitle,
                reviewText,
                selectedMenuTitle,
                selectedImagePath,
                selectedImagePath != null ? 0 : R.drawable.restoran
        );

        if (id != null) {
            reviewDatabase.child(id).setValue(review).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Review berhasil dikirim", Toast.LENGTH_SHORT).show();
                    etReview.setText("");
                    ivPreview.setVisibility(View.GONE);
                    selectedImagePath = null;
                    reviewRecyclerView.smoothScrollToPosition(0);
                } else {
                    Toast.makeText(this, "Gagal mengirim review", Toast.LENGTH_SHORT).show();
                }
            });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_EDIT_REVIEW) {
                if (data.hasExtra("updated_review")) {
                    Review updatedReview = data.getParcelableExtra("updated_review");
                    for (int i = 0; i < reviewList.size(); i++) {
                        if (reviewList.get(i).getId().equals(updatedReview.getId())) {
                            reviewList.set(i, updatedReview);
                            reviewAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                } else if (data.hasExtra("deleted_review_id")) {
                    String deletedId = data.getStringExtra("deleted_review_id");
                    for (int i = 0; i < reviewList.size(); i++) {
                        if (reviewList.get(i).getId().equals(deletedId)) {
                            reviewList.remove(i);
                            reviewAdapter.notifyItemRemoved(i);
                            break;
                        }
                    }
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE || requestCode == REQUEST_IMAGE_PICK) {
                Bitmap bitmap = null;
                if (requestCode == REQUEST_IMAGE_CAPTURE) {
                    Bundle extras = data.getExtras();
                    if (extras != null) bitmap = (Bitmap) extras.get("data");
                } else {
                    Uri imageUri = data.getData();
                    try (InputStream stream = getContentResolver().openInputStream(imageUri)) {
                        bitmap = BitmapFactory.decodeStream(stream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (bitmap != null) {
                    selectedImagePath = saveImageToInternalStorage(bitmap);
                    ivPreview.setImageBitmap(bitmap);
                    ivPreview.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void requestPermissions() {
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        List<String> needed = new ArrayList<>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                needed.add(perm);
            }
        }

        if (!needed.isEmpty()) {
            ActivityCompat.requestPermissions(this, needed.toArray(new String[0]), REQUEST_PERMISSION);
        }
    }

    @Override
    public void onReviewClick(Review review, int position) {
        Intent intent = new Intent(ReviewActivity.this, EditReviewActivity.class);
        intent.putExtra("review", review);
        startActivityForResult(intent, REQUEST_EDIT_REVIEW);
    }

    @Override public void onDeleteClick(Review review, int position) {}
    @Override public void onReviewUpdated(Review review, int position) {}

    @Override
    public void onLikeClick(Review review, int position) {
        review.setLiked(!review.isLiked());
        review.setLikeCount(review.isLiked() ? review.getLikeCount() + 1 : Math.max(0, review.getLikeCount() - 1));
        reviewAdapter.notifyItemChanged(position);
        if (review.getId() != null) {
            reviewDatabase.child(review.getId()).child("liked").setValue(review.isLiked());
            reviewDatabase.child(review.getId()).child("likeCount").setValue(review.getLikeCount());
        }
    }

    @Override
    public void onShareClick(Review review, int position) {
        String text = "Review Menu: " + review.getMenu() + "\n\n" + review.getReview();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(intent, "Bagikan review via"));
    }

    @Override
    public void onBookmarkClick(Review review, int position) {
        review.setBookmarked(!review.isBookmarked());
        if (review.isBookmarked()) {
            bookmarkedReviews.add(review);
            Toast.makeText(this, "Review disimpan!", Toast.LENGTH_SHORT).show();
        } else {
            bookmarkedReviews.remove(review);
            Toast.makeText(this, "Bookmark dibatalkan", Toast.LENGTH_SHORT).show();
        }
        reviewAdapter.notifyItemChanged(position);
        if (review.getId() != null) {
            reviewDatabase.child(review.getId()).child("bookmarked").setValue(review.isBookmarked());
        }
    }

}
