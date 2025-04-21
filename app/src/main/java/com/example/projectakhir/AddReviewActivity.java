package com.example.projectakhir;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddReviewActivity extends AppCompatActivity {

    private EditText etJudul, etIsi, etMenu;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        // Inisialisasi view
        etJudul = findViewById(R.id.et_judul);
        etIsi = findViewById(R.id.et_isi);
        etMenu = findViewById(R.id.et_menu);
        btnSubmit = findViewById(R.id.btn_submit);

        btnSubmit.setOnClickListener(v -> {
            // Validasi input
            if (etJudul.getText().toString().isEmpty() ||
                    etIsi.getText().toString().isEmpty() ||
                    etMenu.getText().toString().isEmpty()) {
                Toast.makeText(this, "Harap isi semua field", Toast.LENGTH_SHORT).show();
                return;
            }

            // Ambil data dari input
            String judul = etJudul.getText().toString();
            String isi = etIsi.getText().toString();
            String menu = etMenu.getText().toString();

            // Generate tanggal sekarang
            String tanggal = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
                    .format(new Date());

            // Kirim data kembali ke ProfileActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("id", String.valueOf(System.currentTimeMillis())); // ID unik
            resultIntent.putExtra("date", tanggal);
            resultIntent.putExtra("title", judul);
            resultIntent.putExtra("review", isi);
            resultIntent.putExtra("menu", menu);
            resultIntent.putExtra("imageRes", R.drawable.ic_user); // Default image


            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}