package com.example.projectakhir;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class InsertJournalActivity extends AppCompatActivity {

    // 1. Deklarasi View dan Variabel
    private TextInputEditText etJudul, etIsi;
    private Button btnSelectImage, btnSubmit, btnBatal;
    private ImageView imagePreview;
    private Toolbar toolbar;
    private ProgressBar progressBar;

    private Bitmap selectedBitmap;
    private DatabaseReference databaseReference;

    // Launcher untuk memilih gambar
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        // Simpan gambar yang dipilih sebagai Bitmap
                        selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        imagePreview.setImageBitmap(selectedBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_journal);

        // 2. Inisialisasi Firebase
        // Pastikan URL dan Path sudah benar
        databaseReference = FirebaseDatabase.getInstance("https://projectakhir-d24d3-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Journal");

        // 3. Inisialisasi semua view
        toolbar = findViewById(R.id.toolbar_insert);
        etJudul = findViewById(R.id.et_judul);
        etIsi = findViewById(R.id.et_isi);
        btnSelectImage = findViewById(R.id.btn_select_image);
        btnSubmit = findViewById(R.id.btn_submit);
        btnBatal = findViewById(R.id.btn_batal);
        imagePreview = findViewById(R.id.image_preview);
        progressBar = findViewById(R.id.progressBar);

        // 4. Setup listener untuk setiap tombol
        toolbar.setNavigationOnClickListener(v -> finish());
        btnSelectImage.setOnClickListener(v -> openGallery());
        btnBatal.setOnClickListener(v -> finish());
        btnSubmit.setOnClickListener(v -> saveJournal());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void saveJournal() {
        String title = etJudul.getText().toString().trim();
        String content = etIsi.getText().toString().trim();

        // 5. Validasi input sebelum menyimpan
        if (title.isEmpty()) {
            etJudul.setError("Judul tidak boleh kosong!");
            return;
        }
        if (content.isEmpty()) {
            etIsi.setError("Isi tidak boleh kosong!");
            return;
        }
        if (selectedBitmap == null) {
            Toast.makeText(this, "Silakan pilih gambar!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tampilkan loading
        progressBar.setVisibility(View.VISIBLE);

        // 6. Konversi gambar Bitmap ke Base64 String
        String imageBase64 = bitmapToBase64(selectedBitmap);

        // 7. Simpan ke Firebase Realtime Database
        String journalId = databaseReference.push().getKey();
        JournalModel journal = new JournalModel(imageBase64, title, content);

        if (journalId != null) {
            databaseReference.child(journalId).setValue(journal)
                    .addOnSuccessListener(aVoid -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(InsertJournalActivity.this, "Jurnal berhasil disimpan!", Toast.LENGTH_SHORT).show();
                        finish(); // Kembali ke halaman sebelumnya
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(InsertJournalActivity.this, "Gagal menyimpan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    // Fungsi helper untuk mengubah Bitmap menjadi String Base64
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // Kompres gambar ke format JPEG dengan kualitas 50 untuk mengurangi ukuran
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}