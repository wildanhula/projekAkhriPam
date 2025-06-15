// JournalAdapter.java
package com.example.projectakhir;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {

    private Context context;
    private List<JournalModel> journalList;

    public JournalAdapter(Context context, List<JournalModel> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_journal, parent, false);
        return new JournalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {
        JournalModel journal = journalList.get(position);

        holder.titleView.setText(journal.getTitle());
        holder.descriptionView.setText(journal.getDescription());

        try {
            byte[] decodedString = Base64.decode(journal.getImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.imageView.setImageBitmap(decodedByte);
        } catch (Exception e) {
            holder.imageView.setImageResource(R.drawable.ic_add_image);
            e.printStackTrace();
        }


        holder.deleteButton.setVisibility(View.GONE);

        // BARU: Tambahkan listener pada seluruh item view untuk membuka halaman edit
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditJournalActivity.class);
            // Kirim semua data yang diperlukan ke EditJournalActivity
            intent.putExtra("JOURNAL_KEY", journal.getKey());
            intent.putExtra("JOURNAL_TITLE", journal.getTitle());
            intent.putExtra("JOURNAL_DESCRIPTION", journal.getDescription());
            intent.putExtra("JOURNAL_IMAGE", journal.getImage());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public static class JournalViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleView;
        TextView descriptionView;
        ImageView deleteButton;

        public JournalViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_journal_image);
            titleView = itemView.findViewById(R.id.tv_journal_title);
            descriptionView = itemView.findViewById(R.id.tv_journal_description);
            deleteButton = itemView.findViewById(R.id.btn_delete);
        }
    }
}