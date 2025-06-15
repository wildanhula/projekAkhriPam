package com.example.projectakhir;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfilActivity extends AppCompatActivity implements ReviewAdapter.OnReviewClickListener {

    private TextView tvNamaLengkap, tvStatus, tvDeskripsi;
    private ImageView profileImage;
    private Button btnEdit, btnLogout;
    private FloatingActionButton fabAdd;
    private RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        // Inisialisasi view
        tvNamaLengkap = findViewById(R.id.tv_namalengkap);
        tvStatus = findViewById(R.id.tv_status);
        tvDeskripsi = findViewById(R.id.tv_deskripsi); // pastikan kamu punya TextView ini di layout
        profileImage = findViewById(R.id.imageView3);
        btnEdit = findViewById(R.id.bt_edit);
        btnLogout = findViewById(R.id.bt_logout);
        fabAdd = findViewById(R.id.fab_add);
        recyclerView = findViewById(R.id.recyclerView);

        // Inisialisasi Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://projectakhir-d24d3-default-rtdb.asia-southeast1.firebasedatabase.app/");
        mDatabase = database.getReference("users");

        // Load profil
        loadProfileData();

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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

        // Tombol Edit Profil
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilActivity.this, edit_profile.class);
            intent.putExtra("nama", tvNamaLengkap.getText().toString());
            intent.putExtra("status", tvStatus.getText().toString());
            intent.putExtra("deskripsi", tvDeskripsi.getText().toString());
            startActivityForResult(intent, 1);
        });

        // Tombol Logout
        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(ProfilActivity.this)
                    .setTitle("Logout")
                    .setMessage("Apakah Anda yakin ingin logout?")
                    .setPositiveButton("Logout", (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(ProfilActivity.this, login_activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        Toast.makeText(this, "Anda berhasil logout", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });

        // FAB Tambah Review
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilActivity.this, AddReviewActivity.class);
            startActivityForResult(intent, 2);
        });

        setupBottomNavigation();
    }

    private void loadProfileData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            mDatabase.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String nama = snapshot.child("nama").getValue(String.class);
                        String status = snapshot.child("status").getValue(String.class);
                        String deskripsi = snapshot.child("deskripsi").getValue(String.class);
                        String fotoUrl = snapshot.child("fotoUrl").getValue(String.class);

                        tvNamaLengkap.setText(nama);
                        tvStatus.setText(status);
                        tvDeskripsi.setText(deskripsi);

                        if (fotoUrl != null && !fotoUrl.isEmpty()) {
                            // Cek apakah ini URI lokal atau URL online
                            if (fotoUrl.startsWith("content://") || fotoUrl.startsWith("file://")) {
                                Glide.with(ProfilActivity.this)
                                        .load(Uri.parse(fotoUrl))
                                        .circleCrop()
                                        .into(profileImage);
                            } else {
                                Glide.with(ProfilActivity.this)
                                        .load(fotoUrl)
                                        .circleCrop()
                                        .into(profileImage);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ProfilActivity.this, "Gagal memuat data profil", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onReviewClick(Review review, int position) {
        showEditDialog(review, position);
    }

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

    @Override
    public void onReviewUpdated(Review review, int position) {
        Toast.makeText(this, "Review updated at position " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLikeClick(Review review, int position) {

    }

    @Override
    public void onShareClick(Review review, int position) {

    }

    @Override
    public void onBookmarkClick(Review review, int position) {

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
            if (requestCode == 1) {
                String updatedNama = data.getStringExtra("nama_baru");
                String updatedStatus = data.getStringExtra("status_baru");
                String updatedDeskripsi = data.getStringExtra("deskripsi_baru");
                String updatedFotoUrl = data.getStringExtra("fotoUrl_baru");

                if (updatedNama != null) tvNamaLengkap.setText(updatedNama);
                if (updatedStatus != null) tvStatus.setText(updatedStatus);
                if (updatedDeskripsi != null) tvDeskripsi.setText(updatedDeskripsi);
                if (updatedFotoUrl != null && !updatedFotoUrl.isEmpty()) {
                    Glide.with(this)
                            .load(updatedFotoUrl)
                            .circleCrop()
                            .into(profileImage);
                }
            } else if (requestCode == 2) {
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
                Intent intent = new Intent(ProfilActivity.this, JournalActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });

            saveIcon.setOnClickListener(v -> {
                Intent intent = new Intent(ProfilActivity.this, FavoriteActivity.class);
                startActivity(intent);
                finish();
            });

            profileIcon.setOnClickListener(v -> {
                // Sudah di halaman profil
            });
        }
    }
