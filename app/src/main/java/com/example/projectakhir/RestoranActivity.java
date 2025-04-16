package com.example.projectakhir;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RestoranActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restoran);

        // Ambil data dari Intent dengan pengecekan null
        Intent intent = getIntent();
        if (intent != null) {
            String foodName = intent.getStringExtra("foodName");
            String foodDesc = intent.getStringExtra("foodDesc");
            int foodImage = intent.getIntExtra("foodImage", -1); // Default -1 jika tidak ada gambar

            // Atur teks nama restoran
            TextView textName = findViewById(R.id.textRestaurantName);
            if (textName != null) {
                textName.setVisibility(View.GONE); // Sembunyikan teks nama restoran
            }

            // Atur deskripsi restoran (jika ada)
            TextView textDesc = findViewById(R.id.foodDesc);
            if (textDesc != null && foodDesc != null) {
                textDesc.setText(foodDesc);
            }

            // Atur gambar restoran (sembunyikan gambar)
            ImageView imageRestaurant = findViewById(R.id.imageViewRestaurant);
            if (imageRestaurant != null) {
                imageRestaurant.setVisibility(View.GONE);
            }
        }

        // Tombol Kirim Review
        Button sendButton = findViewById(R.id.sendButton);
        EditText reviewText = findViewById(R.id.reviewText);

        sendButton.setOnClickListener(v -> {
            String review = reviewText.getText().toString().trim(); // Hindari spasi kosong
            if (!review.isEmpty()) {
                Toast.makeText(RestoranActivity.this, "Review dikirim!", Toast.LENGTH_SHORT).show();
                reviewText.setText(""); // Reset input setelah review dikirim
            } else {
                Toast.makeText(RestoranActivity.this, "Tulis review terlebih dahulu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
