package com.example.projectakhir;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReviewActivity extends AppCompatActivity implements ReviewAdapter.OnReviewClickListener {
    private static final int REQUEST_EDIT_REVIEW = 1;

    private RecyclerView reviewRecyclerView;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;
    private EditText etMenu, etReview;
    private Button btnAddImage, btnSendReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        // Initialize views
        reviewRecyclerView = findViewById(R.id.reviewRecyclerView);
        etMenu = findViewById(R.id.etMenu);
        etReview = findViewById(R.id.etReview);
        btnAddImage = findViewById(R.id.btnAddImage);
        btnSendReview = findViewById(R.id.btnSendReview);

        // Setup RecyclerView
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviewList, this);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewRecyclerView.setAdapter(reviewAdapter);

        // Add some sample reviews
        addSampleReviews();

        // Set click listeners
        btnSendReview.setOnClickListener(v -> submitReview());
        btnAddImage.setOnClickListener(v -> addImage());

        // Bottom navigation setup
        setupBottomNavigation();
    }

    private void addSampleReviews() {
        reviewList.add(new Review(
                "1",
                new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date()),
                "Baklava",
                "Rasanya manis bikin gigi ngilu, Tapi rasanya tetep enak kok!",
                "Baklava",
                R.drawable.baklava
        ));
        reviewList.add(new Review(
                "2",
                new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date()),
                "Pasta Carbonara",
                "Pasta dengan saus krim yang sangat lezat dan gurih",
                "Pasta Carbonara",
                R.drawable.pasta
        ));
        reviewAdapter.notifyDataSetChanged();
    }

    private void submitReview() {
        String menu = etMenu.getText().toString().trim();
        String reviewText = etReview.getText().toString().trim();

        if (menu.isEmpty() || reviewText.isEmpty()) {
            Toast.makeText(this, "Menu dan review tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create new review
        String currentDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date());
        Review newReview = new Review(
                String.valueOf(reviewList.size() + 1),
                currentDate,
                menu,
                reviewText,
                menu,
                R.drawable.restoran
        );

        // Add to list and update adapter
        reviewList.add(0, newReview);
        reviewAdapter.notifyItemInserted(0);
        reviewRecyclerView.smoothScrollToPosition(0);

        // Clear input fields
        etMenu.setText("");
        etReview.setText("");

        Toast.makeText(this, "Review berhasil dikirim", Toast.LENGTH_SHORT).show();
    }

    private void addImage() {
        Toast.makeText(this, "Fitur tambah gambar akan diimplementasi nanti", Toast.LENGTH_SHORT).show();
    }

    private void setupBottomNavigation() {
        ImageView iconHome = findViewById(R.id.icon_home);
        ImageView iconNotification = findViewById(R.id.icon_notification);
        ImageView iconSave = findViewById(R.id.icon_save);
        ImageView iconProfile = findViewById(R.id.icon_profile);

        iconHome.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        iconNotification.setOnClickListener(v -> {
            Toast.makeText(this, "Notifikasi", Toast.LENGTH_SHORT).show();
        });

        iconSave.setOnClickListener(v -> {
            startActivity(new Intent(this, FavoriteActivity.class));
            finish();
        });

        iconProfile.setOnClickListener(v -> {
            Toast.makeText(this, "Profil", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onReviewClick(Review review, int position) {

    }

    @Override
    public void onDeleteClick(Review review, int position) {

    }

    @Override
    public void onReviewUpdated(Review review, int position) {
        if (review == null) {
            // Delete review
            reviewList.remove(position);
            reviewAdapter.notifyItemRemoved(position);
            Toast.makeText(this, "Review dihapus", Toast.LENGTH_SHORT).show();
        } else {
            // Update review
            reviewList.set(position, review);
            reviewAdapter.notifyItemChanged(position);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EDIT_REVIEW && resultCode == RESULT_OK && data != null) {
            Review updatedReview = (Review) data.getSerializableExtra("updatedReview");
            int position = data.getIntExtra("position", -1);

            if (updatedReview != null && position != -1) {
                onReviewUpdated(updatedReview, position);
            }
        }
    }
}