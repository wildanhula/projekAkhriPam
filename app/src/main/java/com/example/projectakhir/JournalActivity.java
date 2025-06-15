// JournalActivity.java
package com.example.projectakhir;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JournalActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private JournalAdapter adapter;
    private List<JournalModel> journalList;
    private FloatingActionButton fabAdd;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        ImageView homeIcon = findViewById(R.id.imgHome);
//        ImageView notificationIcon = findViewById(R.id.imgn=);
        ImageView saveIcon = findViewById(R.id.imgSave);
        ImageView profileIcon = findViewById(R.id.imgProfil);


        homeIcon.setOnClickListener(v -> {
            Intent intent = new Intent(JournalActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // optional, if you don't want user to go back
        });

//        notificationIcon.setOnClickListener(v -> {
//            Intent intent = new Intent(ReviewActivity.this, NotificationActivity.class);
//            startActivity(intent);
//            Toast.makeText(this, "Fitur belum tersedia", Toast.LENGTH_SHORT).show();
//
//        });

//        journalIcon.setOnClickListener(v -> {
//            Intent intent = new Intent(ReviewActivity.this, JournalActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            startActivity(intent);
//            finish();
//        });

        saveIcon.setOnClickListener(v -> {
            Intent intent = new Intent(JournalActivity.this, FavoriteActivity.class);
            startActivity(intent);
            finish();
        });
        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(JournalActivity.this, ProfilActivity.class);
            startActivity(intent);
        });

        recyclerView = findViewById(R.id.rv_journal);
        fabAdd = findViewById(R.id.fab_add_journal);
        // Pastikan Anda menambahkan ProgressBar dengan id 'progressBar' di file XML Anda
        // progressBar = findViewById(R.id.progressBar);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        journalList = new ArrayList<>();
        adapter = new JournalAdapter(this, journalList);
        recyclerView.setAdapter(adapter);

        // Inisialisasi Firebase Database
        databaseReference = FirebaseDatabase.getInstance("https://projectakhir-d24d3-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Journal");

        // Ambil data dari Firebase
        fetchJournalData();

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(JournalActivity.this, InsertJournalActivity.class);
            startActivity(intent);
        });
    }

    private void fetchJournalData() {
        // if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                journalList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    JournalModel journal = snapshot.getValue(JournalModel.class);
                    if (journal != null) {
                        journal.setKey(snapshot.getKey()); // Simpan key untuk operasi hapus
                        journalList.add(journal);
                    }
                }
                Collections.reverse(journalList); // Tampilkan data terbaru di atas
                adapter.notifyDataSetChanged();
                // if (progressBar != null) progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(JournalActivity.this, "Gagal memuat data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}