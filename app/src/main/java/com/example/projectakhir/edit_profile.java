package com.example.projectakhir;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class edit_profile extends AppCompatActivity {
    private EditText edtNama, edtStatus;
    private Button btnSimpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Inisialisasi komponen
        edtNama = findViewById(R.id.edt_title);
        edtStatus = findViewById(R.id.edt_status);
        btnSimpan = findViewById(R.id.btn_submit);

        // Ambil data dari Intent
        Intent intent = getIntent();
        String nama = intent.getStringExtra("nama");
        String status = intent.getStringExtra("status");

        // Set data ke EditText
        edtNama.setText(nama);
        edtStatus.setText(status);

        // Tombol Simpan
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ambil data yang diinputkan user
                String namaBaru = edtNama.getText().toString();
                String statusBaru = edtStatus.getText().toString();

                // Kirim kembali ke MainActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("nama_baru", namaBaru);
                resultIntent.putExtra("status_baru", statusBaru);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}
