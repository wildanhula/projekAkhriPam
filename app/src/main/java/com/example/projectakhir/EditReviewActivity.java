package com.example.projectakhir;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class EditReviewActivity extends AppCompatActivity {

    private TextInputEditText etJudul, etIsi, etMenu;
    private Button btnSubmit;
    private Review review;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_review);

        // Initialize views
        etJudul = findViewById(R.id.et_judul);
        etIsi = findViewById(R.id.et_isi);
        etMenu = findViewById(R.id.et_menu);
        btnSubmit = findViewById(R.id.btn_submit);

        // Get review data from intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("review") && intent.hasExtra("position")) {
            review = (Review) intent.getSerializableExtra("review");
            position = intent.getIntExtra("position", -1);

            // Populate fields with review data
            if (review != null) {
                etJudul.setText(review.getTitle());
                etIsi.setText(review.getReview());
                etMenu.setText(review.getMenu());
            }
        } else {
            Toast.makeText(this, "Data review tidak valid", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set click listener for submit button
        btnSubmit.setOnClickListener(v -> updateReview());
    }

    private void updateReview() {
        String judul = etJudul.getText().toString().trim();
        String isi = etIsi.getText().toString().trim();
        String menu = etMenu.getText().toString().trim();

        if (judul.isEmpty() || isi.isEmpty() || menu.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update review object
        review.setTitle(judul);
        review.setReview(isi);
        review.setMenu(menu);
        review.updateDate(); // Update the timestamp

        // Return updated review to calling activity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedReview", review);
        resultIntent.putExtra("position", position);
        setResult(RESULT_OK, resultIntent);
        finish();

        Toast.makeText(this, "Review berhasil diperbarui", Toast.LENGTH_SHORT).show();
    }
}