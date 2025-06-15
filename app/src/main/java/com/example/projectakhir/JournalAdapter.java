package com.example.projectakhir;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        holder.imageView.setImageResource(journal.getImage());
        holder.titleView.setText(journal.getTitle());
        holder.descriptionView.setText(journal.getDescription());

        holder.deleteButton.setOnClickListener(v -> {
            // Logika untuk menghapus item
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                journalList.remove(currentPosition);
                notifyItemRemoved(currentPosition);
                Toast.makeText(context, "Jurnal " + journal.getTitle() + " dihapus", Toast.LENGTH_SHORT).show();
            }
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
