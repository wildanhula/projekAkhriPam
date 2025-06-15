package com.example.projectakhir;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class JournalActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private JournalAdapter adapter;
    private List<JournalModel> journalList;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        // Inisialisasi View
        recyclerView = findViewById(R.id.rv_journal);
        fabAdd = findViewById(R.id.fab_add_journal);

        // Siapkan data dummy
        prepareJournalData();

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new JournalAdapter(this, journalList);
        recyclerView.setAdapter(adapter);

        // Setup Bottom Navigation
//        setupBottomNavigation();

        // Listener untuk tombol tambah jurnal
        fabAdd.setOnClickListener(v -> {
            // Intent ke halaman tambah jurnal baru
            Intent intent = new Intent(JournalActivity.this, InsertJournalActivity.class);
            startActivity(intent);
        });
    }

    private void setupBottomNavigation() {
        ImageView homeIcon = findViewById(R.id.home_icon);
        ImageView notificationIcon = findViewById(R.id.notification_icon);
        ImageView journalIcon = findViewById(R.id.journal_icon);
        ImageView saveIcon = findViewById(R.id.imgSave);
        ImageView profileIcon = findViewById(R.id.profile_icon);

        homeIcon.setOnClickListener(v -> {
            Intent intent = new Intent(JournalActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        notificationIcon.setOnClickListener(v -> {
            Toast.makeText(this, "Notification clicked", Toast.LENGTH_SHORT).show();
        });

        journalIcon.setOnClickListener(v -> {
            Toast.makeText(this, "Kamu sudah di halaman jurnal", Toast.LENGTH_SHORT).show();
        });

        saveIcon.setOnClickListener(v -> {
            Intent intent = new Intent(JournalActivity.this, FavoriteActivity.class);
            startActivity(intent);
            finish();
        });

        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(JournalActivity.this, ProfilActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void prepareJournalData() {
        journalList = new ArrayList<>();
        journalList.add(new JournalModel(R.drawable.lalapan, "Gizi pada Lalapan",
                "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."));
        journalList.add(new JournalModel(R.drawable.rendang, "Rendang Makanan no 1",
                "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."));
        journalList.add(new JournalModel(R.drawable.soto, "Soto Ayam Lamongan",
                "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."));
    }
}
