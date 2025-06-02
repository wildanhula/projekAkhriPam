package com.example.projectakhir;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class edit_profile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText edtNama, edtStatus, edtDescription;
    private Button btnSimpan, btnEditProfilePicture;
    private ImageView imgUploadGambar;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://projectakhir-d24d3-default-rtdb.asia-southeast1.firebasedatabase.app/"
        );
        mDatabase = database.getReference("users");

        // Inisialisasi view
        edtNama = findViewById(R.id.edt_title);
        edtStatus = findViewById(R.id.edt_status);
        edtDescription = findViewById(R.id.edt_description);
        btnSimpan = findViewById(R.id.btn_submit);
        imgUploadGambar = findViewById(R.id.img_uploadgambar);
        btnEditProfilePicture = findViewById(R.id.btn_edit_profile_picture);

        // Load data profil dari database
        loadProfileData();

        // Event click untuk pilih gambar via button
        btnEditProfilePicture.setOnClickListener(v -> openFileChooser());

        // Event click untuk simpan perubahan profil
        btnSimpan.setOnClickListener(v -> updateProfile());
    }

    /**
     * Buka gallery untuk memilih gambar
     */
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // Tampilkan gambar terpilih dengan Glide
            Glide.with(this).load(imageUri).into(imgUploadGambar);
        }
    }

    /**
     * Memuat data profil dari Firebase Realtime Database
     */
    private void loadProfileData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User belum login", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    edtNama.setText(snapshot.child("nama").getValue(String.class));
                    edtStatus.setText(snapshot.child("status").getValue(String.class));
                    edtDescription.setText(snapshot.child("deskripsi").getValue(String.class));
                    String fotoUrl = snapshot.child("fotoUrl").getValue(String.class);
                    if (fotoUrl != null && !fotoUrl.isEmpty()) {
                        // Cek apakah URI lokal atau URL online
                        if (fotoUrl.startsWith("content://") || fotoUrl.startsWith("file://")) {
                            imageUri = Uri.parse(fotoUrl);
                            Glide.with(edit_profile.this).load(imageUri).into(imgUploadGambar);
                        } else {
                            Glide.with(edit_profile.this).load(fotoUrl).into(imgUploadGambar);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(edit_profile.this, "Gagal memuat data profil", Toast.LENGTH_SHORT).show();
                Log.e("loadProfileData", error.getMessage());
            }
        });
    }

    /**
     * Proses update profil ke Firebase Realtime Database
     */
    private void updateProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Anda belum login", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        String namaBaru = edtNama.getText().toString().trim();
        String statusBaru = edtStatus.getText().toString().trim();
        String deskripsiBaru = edtDescription.getText().toString().trim();

        if (namaBaru.isEmpty()) {
            edtNama.setError("Nama tidak boleh kosong");
            edtNama.requestFocus();
            return;
        }

        // Simpan URI gambar sebagai string jika ada
        String imageUriString = (imageUri != null) ? imageUri.toString() : null;

        // Simpan semua data ke database
        saveProfileToDatabase(userId, namaBaru, statusBaru, deskripsiBaru, imageUriString);
    }

    /**
     * Simpan data profil ke Firebase Realtime Database
     */
    private void saveProfileToDatabase(String userId, String nama, String status, String deskripsi, String fotoUrl) {
        Map<String, Object> profileUpdates = new HashMap<>();
        profileUpdates.put("nama", nama);
        profileUpdates.put("status", status);
        profileUpdates.put("deskripsi", deskripsi);

        // Hanya update fotoUrl jika ada gambar baru
        if (fotoUrl != null) {
            profileUpdates.put("fotoUrl", fotoUrl);
        }

        mDatabase.child(userId).updateChildren(profileUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(edit_profile.this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show();

                // Kirim hasil kembali ke ProfilActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("nama_baru", nama);
                resultIntent.putExtra("status_baru", status);
                resultIntent.putExtra("deskripsi_baru", deskripsi);
                if (fotoUrl != null) {
                    resultIntent.putExtra("fotoUrl_baru", fotoUrl);
                }
                setResult(RESULT_OK, resultIntent);

                finish(); // Kembali ke activity sebelumnya
            } else {
                Toast.makeText(edit_profile.this, "Gagal memperbarui profil", Toast.LENGTH_SHORT).show();
                Log.e("saveProfileToDatabase", task.getException() != null ? task.getException().getMessage() : "Unknown error");
            }
        });
    }
}