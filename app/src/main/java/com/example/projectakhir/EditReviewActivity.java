package com.example.projectakhir;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;

public class EditReviewActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 101;

    private TextInputEditText etJudul, etIsi, etMenu;
    private ImageView imagePreview;
    private Button btnSubmit, btnDelete;

    private Review review;
    private int position;
    private String newImagePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_review);

        etJudul = findViewById(R.id.et_judul);
        etIsi = findViewById(R.id.et_isi);
        etMenu = findViewById(R.id.et_menu);
        imagePreview = findViewById(R.id.image_preview);
        btnSubmit = findViewById(R.id.btn_submit);
        btnDelete = findViewById(R.id.btn_delete);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("review") && intent.hasExtra("position")) {
            review = (Review) intent.getSerializableExtra("review");
            position = intent.getIntExtra("position", -1);

            if (review != null) {
                etJudul.setText(review.getTitle());
                etIsi.setText(review.getReview());
                etMenu.setText(review.getMenu());

                if (review.hasCustomImage()) {
                    File file = new File(review.getImagePath());
                    if (file.exists()) {
                        imagePreview.setImageURI(Uri.fromFile(file));
                    }
                } else {
                    imagePreview.setImageResource(review.getImageRes());
                }
            }
        } else {
            Toast.makeText(this, "Data review tidak valid", Toast.LENGTH_SHORT).show();
            finish();
        }

        imagePreview.setOnClickListener(v -> openGallery());
        btnSubmit.setOnClickListener(v -> updateReview());
        btnDelete.setOnClickListener(v -> deleteReview());
    }

    private void openGallery() {
        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImage.setType("image/*");
        startActivityForResult(pickImage, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            newImagePath = FileUtils.getPath(this, selectedImageUri); // kamu harus implement FileUtils
            imagePreview.setImageURI(selectedImageUri);
        }
    }

    private void updateReview() {
        String judul = etJudul.getText().toString().trim();
        String isi = etIsi.getText().toString().trim();
        String menu = etMenu.getText().toString().trim();

        if (judul.isEmpty() || isi.isEmpty() || menu.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        review.setTitle(judul);
        review.setReview(isi);
        review.setMenu(menu);
        review.updateDate();

        if (newImagePath != null) {
            review.setImagePath(newImagePath);
            review.setImageRes(0); // reset default image
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedReview", review);
        resultIntent.putExtra("position", position);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void deleteReview() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("position", position);
        setResult(RESULT_FIRST_USER, resultIntent);
        finish();
    }
}
