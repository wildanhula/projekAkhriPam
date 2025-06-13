package com.example.projectakhir;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<Review> reviewList;
    private OnReviewClickListener listener;
    private static final int REQUEST_EDIT_REVIEW = 1001;

    public interface OnReviewClickListener {
        void onReviewClick(Review review, int position);
        void onDeleteClick(Review review, int position);
        void onReviewUpdated(Review updatedReview, int position);
        void onLikeClick(Review review, int position);
        void onShareClick(Review review, int position);
        void onBookmarkClick(Review review, int position);
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

        if (review.hasCustomImage()) {
            loadCustomImage(holder.imageGambar, review.getImagePath());
        } else {
            holder.imageGambar.setImageResource(review.getImageRes());
        }

        // Set status dan listener untuk tombol Like
        if (holder.btnLike != null) {
            holder.btnLike.setSelected(review.isLiked());
            holder.btnLike.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLikeClick(review, position);
                    notifyItemChanged(position); // Refresh icon
                }
            });
        }

        // Set status dan listener untuk tombol Bookmark
        if (holder.btnBookmark != null) {
            holder.btnBookmark.setSelected(review.isBookmarked());
            holder.btnBookmark.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBookmarkClick(review, position);
                    notifyItemChanged(position); // Refresh icon
                }
            });
        }

        // Listener tombol Share
        if (holder.btnShare != null) {
            holder.btnShare.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onShareClick(review, position);
                }
            });
        }

        // Like count
        if (holder.tvLikeCount != null) {
            holder.tvLikeCount.setText(String.valueOf(review.getLikeCount()));
        }

        // Edit
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                Intent intent = new Intent(v.getContext(), EditReviewActivity.class);
                intent.putExtra("review", review);
                intent.putExtra("position", position);
                ((Activity) v.getContext()).startActivityForResult(intent, REQUEST_EDIT_REVIEW);
            }
        });

        // Delete
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(review, position);
                return true;
            }
            return false;
        });
    }

    private void loadCustomImage(ImageView imageView, String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            imageView.setImageResource(R.drawable.restoran);
            return;
        }

        try {
            File file = new File(imagePath);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    imageView.setImageResource(R.drawable.restoran);
                }
            } else {
                imageView.setImageResource(R.drawable.restoran);
            }
        } catch (Exception e) {
            imageView.setImageResource(R.drawable.restoran);
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public void updateData(List<Review> newReviews) {
        reviewList = newReviews;
        notifyDataSetChanged();
    }

    public void updateReview(Review updatedReview, int position) {
        if (position >= 0 && position < reviewList.size()) {
            reviewList.set(position, updatedReview);
            notifyItemChanged(position);
        }
    }

    public void removeReview(int position) {
        if (position >= 0 && position < reviewList.size()) {
            reviewList.remove(position);
            notifyItemRemoved(position);
        }
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView textDate, textJudul, textReview, textMenu, tvLikeCount;
        ImageView imageGambar;
        ImageButton btnLike, btnShare, btnBookmark;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.text_date);
            textJudul = itemView.findViewById(R.id.text_judul);
            textReview = itemView.findViewById(R.id.text_review);
            textMenu = itemView.findViewById(R.id.text_menu);
            imageGambar = itemView.findViewById(R.id.image_gambar);

            try {
                btnLike = itemView.findViewById(R.id.btnLike);
                btnShare = itemView.findViewById(R.id.btnShare);
                btnBookmark = itemView.findViewById(R.id.btnBookmark);
                tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            } catch (Exception e) {
                btnLike = null;
                btnShare = null;
                btnBookmark = null;
                tvLikeCount = null;
            }
        }
    }
}
