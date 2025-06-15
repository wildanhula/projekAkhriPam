package com.example.projectakhir;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddReviewActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 101;

    private EditText etJudul, etIsi, etMenu;
    private Button btnSubmit, btnPilihGambar;
    private ImageView imgReview;
    private Uri imageUri;
    private Bitmap selectedBitmap;

    private DatabaseReference reviewRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        // Inisialisasi view
        etJudul = findViewById(R.id.et_judul);
        etIsi = findViewById(R.id.et_isi);
        etMenu = findViewById(R.id.et_menu);
        btnSubmit = findViewById(R.id.btn_submit);
        btnPilihGambar = findViewById(R.id.btn_pilih_gambar);
        imgReview = findViewById(R.id.img_review);

        // Firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            reviewRef = FirebaseDatabase.getInstance("https://projectakhir-d24d3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("users").child(uid).child("reviews");
        } else {
            Toast.makeText(this, "User belum login", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnPilihGambar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_IMAGE_PICK);
        });

        btnSubmit.setOnClickListener(v -> {
            String judul = etJudul.getText().toString().trim();
            String isi = etIsi.getText().toString().trim();
            String menu = etMenu.getText().toString().trim();

            if (judul.isEmpty() || isi.isEmpty() || menu.isEmpty()) {
                Toast.makeText(this, "Harap isi semua field", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedBitmap == null) {
                Toast.makeText(this, "Harap pilih gambar", Toast.LENGTH_SHORT).show();
                return;
            }

            // Encode bitmap to base64
            String imageBase64 = encodeImageToBase64(selectedBitmap);

            // Buat dan simpan review
            String id = String.valueOf(System.currentTimeMillis());
            String tanggal = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date());

            Review newReview = new Review(id, tanggal, judul, isi, menu, imageBase64, 0);

            reviewRef.child(id).setValue(newReview)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Review berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Gagal menambahkan review", Toast.LENGTH_SHORT).show());
        });
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream); // Kompresi
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imgReview.setImageBitmap(selectedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
