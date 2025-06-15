package com.example.projectakhir;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class InsertJournalActivity extends AppCompatActivity {

    private EditText etTitle, etContent;
    private Button btnSave;
    private ImageView imgPreview, btnBack;

    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_journal);

        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        btnSave = findViewById(R.id.btn_save);
        imgPreview = findViewById(R.id.img_preview);
        btnBack = findViewById(R.id.btn_back);

        // Pilih gambar dari galeri
        imgPreview.setOnClickListener(v -> openGallery());

        // Tombol kembali
        btnBack.setOnClickListener(v -> finish());

        // Simpan jurnal
        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String content = etContent.getText().toString().trim();

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Judul dan isi tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            } else if (selectedImageUri == null) {
                Toast.makeText(this, "Silakan pilih gambar!", Toast.LENGTH_SHORT).show();
            } else {
                // Simulasi penyimpanan
                Toast.makeText(this, "Jurnal berhasil disimpan!", Toast.LENGTH_SHORT).show();
                // Simpan ke database, API, dsb.
                finish();
            }
        });
    }

    // Launcher untuk memilih gambar
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                        imgPreview.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }
}
