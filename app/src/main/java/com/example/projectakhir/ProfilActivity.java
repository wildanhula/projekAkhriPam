package com.example.projectakhir;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ProfilActivity extends AppCompatActivity implements ReviewAdapter.OnReviewClickListener {
    private TextView tvNamaLengkap, tvStatus;
    private ImageView profileImage;
    private Button btnEdit, btnLogout;
    private FloatingActionButton fabAdd;
    private RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        // Initialize Views
        tvNamaLengkap = findViewById(R.id.tv_namalengkap);
        tvStatus = findViewById(R.id.tv_status);
        profileImage = findViewById(R.id.imageView3);
        btnEdit = findViewById(R.id.bt_edit);
        btnLogout = findViewById(R.id.bt_logout);
        fabAdd = findViewById(R.id.fab_add);
        recyclerView = findViewById(R.id.recyclerView);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Dummy review data
        reviewList = new ArrayList<>();
        reviewList.add(new Review("1", "2025/04/06 19:30:38", "Baklava",
                "Rasanya manis bikin gigi ngilu, Tapi rasanya tetep enak kok!",
                "Baklava", R.drawable.baklava));
        reviewList.add(new Review("2", "2025/04/06 19:32:04", "Coklat",
                "Coklat terfavorit banyak rasa",
                "Coklat", R.drawable.coklat));
        reviewList.add(new Review("3", "2025/04/06 19:32:04", "Bakwan",
                "Bakwan rasa yang mantap sekali",
                "Bakwan", R.drawable.bakwan));

        reviewAdapter = new ReviewAdapter(reviewList, this);
        recyclerView.setAdapter(reviewAdapter);

        // Edit Profile Button
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilActivity.this, edit_profile.class);
            intent.putExtra("nama", tvNamaLengkap.getText().toString());
            intent.putExtra("status", tvStatus.getText().toString());
            startActivityForResult(intent, 1);
        });

        // Logout Button
        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(ProfilActivity.this)
                    .setTitle("Logout")
                    .setMessage("Apakah Anda yakin ingin logout?")
                    .setPositiveButton("Logout", (dialog, which) -> {
                        // Firebase logout
                        com.google.firebase.auth.FirebaseAuth.getInstance().signOut();

                        // Return to login screen and clear back stack
                        Intent intent = new Intent(ProfilActivity.this, login_activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                        Toast.makeText(this, "Anda berhasil logout", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });

        // Add Review FAB
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilActivity.this, AddReviewActivity.class);
            startActivityForResult(intent, 2);
        });

        // Bottom Navigation
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        ImageView homeIcon = findViewById(R.id.home_icon);
        ImageView notificationIcon = findViewById(R.id.notification_icon);
        ImageView journalIcon = findViewById(R.id.journal_icon);
        ImageView saveIcon = findViewById(R.id.imgSave);
        ImageView profileIcon = findViewById(R.id.profile_icon);

        profileIcon.setColorFilter(getResources().getColor(android.R.color.holo_orange_dark));

        homeIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        notificationIcon.setOnClickListener(v -> {
            Toast.makeText(this, "Notification clicked", Toast.LENGTH_SHORT).show();
        });

        journalIcon.setOnClickListener(v -> {
            Toast.makeText(this, "Journal clicked", Toast.LENGTH_SHORT).show();
        });

        saveIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilActivity.this, FavoriteActivity.class);
            startActivity(intent);
            finish();
        });

        profileIcon.setOnClickListener(v -> {
            // Already in profile
        });
    }

    // Handle review click
    @Override
    public void onReviewClick(Review review, int position) {
        showEditDialog(review, position);
    }

    // Handle delete click
    @Override
    public void onDeleteClick(Review review, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Review")
                .setMessage("Are you sure you want to delete this review?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    reviewList.remove(position);
                    reviewAdapter.notifyItemRemoved(position);
                    Toast.makeText(this, "Review deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // New: Implement the missing method
    @Override
    public void onReviewUpdated(Review review, int position) {
        // Handle review update (e.g., sync with database)
        Toast.makeText(this, "Review updated at position " + position, Toast.LENGTH_SHORT).show();
    }

    private void showEditDialog(Review review, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Review");

        View view = getLayoutInflater().inflate(R.layout.dialog_edit_review, null);
        EditText etReview = view.findViewById(R.id.et_review);
        etReview.setText(review.getReview());

        builder.setView(view);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newReview = etReview.getText().toString().trim();
            if (!newReview.isEmpty()) {
                review.setReview(newReview);
                reviewAdapter.notifyItemChanged(position);
                Toast.makeText(this, "Review updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Review cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 1) { // Edit profile
                String updatedNama = data.getStringExtra("nama_baru");
                String updatedStatus = data.getStringExtra("status_baru");

                if (updatedNama != null) tvNamaLengkap.setText(updatedNama);
                if (updatedStatus != null) tvStatus.setText(updatedStatus);

            } else if (requestCode == 2) { // Add review
                Review newReview = new Review(
                        data.getStringExtra("id"),
                        data.getStringExtra("date"),
                        data.getStringExtra("title"),
                        data.getStringExtra("review"),
                        data.getStringExtra("menu"),
                        data.getIntExtra("imageRes", R.drawable.ic_user)
                );

                reviewList.add(0, newReview);
                reviewAdapter.notifyItemInserted(0);
                recyclerView.smoothScrollToPosition(0);
            }
        }
    }
}