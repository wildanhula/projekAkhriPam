package com.example.projectakhir;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditJournalActivity extends AppCompatActivity {

    private TextInputEditText etJudul, etIsi;
    private Button btnChangeImage, btnUpdate, btnDelete;
    private ImageView imagePreview;
    private Toolbar toolbar;
    private ProgressBar progressBar;

    private DatabaseReference databaseReference;
    private String journalKey, currentImageBase64;
    private Bitmap selectedBitmap;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        imagePreview.setImageBitmap(selectedBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_journal);

        // Inisialisasi Views
        toolbar = findViewById(R.id.toolbar_edit);
        etJudul = findViewById(R.id.et_judul_edit);
        etIsi = findViewById(R.id.et_isi_edit);
        btnChangeImage = findViewById(R.id.btn_change_image_edit);
        btnUpdate = findViewById(R.id.btn_update);
        btnDelete = findViewById(R.id.btn_delete_from_edit);
        imagePreview = findViewById(R.id.image_preview_edit);
        progressBar = findViewById(R.id.progressBarEdit);

        // Inisialisasi Firebase
        databaseReference = FirebaseDatabase.getInstance("https://projectakhir-d24d3-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Journal");

        // Ambil data yang dikirim dari JournalAdapter
        Intent intent = getIntent();
        journalKey = intent.getStringExtra("JOURNAL_KEY");
        currentImageBase64 = intent.getStringExtra("JOURNAL_IMAGE");
        String title = intent.getStringExtra("JOURNAL_TITLE");
        String description = intent.getStringExtra("JOURNAL_DESCRIPTION");

        // Tampilkan data yang ada ke form
        etJudul.setText(title);
        etIsi.setText(description);
        if (currentImageBase64 != null && !currentImageBase64.isEmpty()) {
            imagePreview.setImageBitmap(base64ToBitmap(currentImageBase64));
        }

        // Setup Listeners
        toolbar.setNavigationOnClickListener(v -> finish());
        btnChangeImage.setOnClickListener(v -> openGallery());
        btnUpdate.setOnClickListener(v -> updateJournal());
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void updateJournal() {
        String newTitle = etJudul.getText().toString().trim();
        String newContent = etIsi.getText().toString().trim();

        if (newTitle.isEmpty() || newContent.isEmpty()) {
            Toast.makeText(this, "Judul dan isi tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        String imageToSave = currentImageBase64;
        // Jika pengguna memilih gambar baru, konversi ke Base64
        if (selectedBitmap != null) {
            imageToSave = bitmapToBase64(selectedBitmap);
        }

        // Gunakan Map untuk update, ini lebih efisien daripada membuat objek baru
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", newTitle);
        updates.put("description", newContent);
        updates.put("image", imageToSave);

        databaseReference.child(journalKey).updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Jurnal berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Gagal memperbarui: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Jurnal")
                .setMessage("Apakah Anda yakin ingin menghapus jurnal ini secara permanen?")
                .setPositiveButton("Hapus", (dialog, which) -> deleteJournal())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteJournal() {
        databaseReference.child(journalKey).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Jurnal berhasil dihapus", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Gagal menghapus jurnal", Toast.LENGTH_SHORT).show());
    }

    // Helper Functions
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private Bitmap base64ToBitmap(String base64String) {
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}