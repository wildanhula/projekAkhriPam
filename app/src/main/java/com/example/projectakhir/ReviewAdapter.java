package com.example.projectakhir;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<Review> reviewList;
    private OnReviewClickListener listener;

    public interface OnReviewClickListener {
        void onReviewClick(Review review, int position);
        void onDeleteClick(Review review, int position);
    }

    public ReviewAdapter(List<Review> reviewList, OnReviewClickListener listener) {
        this.reviewList = reviewList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);

        holder.textDate.setText(review.getDate());
        holder.textJudul.setText(review.getTitle());
        holder.textReview.setText(review.getReview());
        holder.textMenu.setText("Menu: " + review.getMenu());
        holder.imageGambar.setImageResource(review.getImageRes());

        // Click listener untuk edit
        holder.imageGambar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReviewClick(review, position);
            }
        });

        // Long click listener untuk delete
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(review, position);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public void updateData(List<Review> newReviews) {
        reviewList = newReviews;
        notifyDataSetChanged();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView textDate, textJudul, textReview, textMenu;
        ImageView imageGambar;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.text_date);
            textJudul = itemView.findViewById(R.id.text_judul);
            textReview = itemView.findViewById(R.id.text_review);
            textMenu = itemView.findViewById(R.id.text_menu);
            imageGambar = itemView.findViewById(R.id.image_gambar);
        }
    }
}