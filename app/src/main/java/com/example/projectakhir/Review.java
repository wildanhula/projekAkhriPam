package com.example.projectakhir;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Review implements Serializable {
    private String id;
    private String date;
    private String title;
    private String review;
    private String menu;
    private String imagePath; // Local image path
    private int imageRes; // Default image fallback


    // FIELD BARU - Untuk fitur Like, Share, Bookmark
    private boolean isLiked = false;
    private int likeCount = 0;
    private boolean isBookmarked = false;

    public Review() {
        // Diperlukan untuk Firebase
        this.isLiked = false;
        this.likeCount = 0;
        this.isBookmarked = false;
    }

    public Review(String id, String date, String title, String review, String menu, String imagePath, int imageRes) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.review = review;
        this.menu = menu;
        this.imagePath = imagePath;
        this.imageRes = imageRes;
        // INISIALISASI BARU - Set default value untuk field baru
        this.isLiked = false;
        this.likeCount = 0;
        this.isBookmarked = false;
    }

    public Review(String id, String date, String title, String review, String menu, int imageRes) {
        this(id, date, title, review, menu, null, imageRes);
    }

    // Getter yang sudah ada
    public String getId() { return id; }
    public String getDate() { return date; }
    public String getTitle() { return title; }
    public String getReview() { return review; }
    public String getMenu() { return menu; }
    public String getImagePath() { return imagePath; }
    public int getImageRes() { return imageRes; }

    // Setter yang sudah ada
    public void setId(String id) { this.id = id; }
    public void setDate(String date) { this.date = date; }
    public void setTitle(String title) { this.title = title; }
    public void setReview(String review) { this.review = review; }
    public void setMenu(String menu) { this.menu = menu; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setImageRes(int imageRes) { this.imageRes = imageRes; }

    // GETTER BARU - Untuk fitur Like, Share, Bookmark
    public boolean isLiked() {
        return isLiked;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    // SETTER BARU - Untuk fitur Like, Share, Bookmark
    public void setLiked(boolean liked) {
        this.isLiked = liked;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = Math.max(0, likeCount); // Pastikan tidak negatif
    }

    public void setBookmarked(boolean bookmarked) {
        this.isBookmarked = bookmarked;
    }

    // METHOD BARU - Helper methods untuk fitur baru
    public void toggleLike() {
        this.isLiked = !this.isLiked;
        if (this.isLiked) {
            this.likeCount++;
        } else {
            this.likeCount = Math.max(0, this.likeCount - 1);
        }
    }

    public void toggleBookmark() {
        this.isBookmarked = !this.isBookmarked;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }

    // Method yang sudah ada
    public void updateDate() {
        this.date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    public boolean hasCustomImage() {
        return imagePath != null && !imagePath.trim().isEmpty();
    }

    // METHOD BARU - Untuk keperluan sharing
    public String getShareText() {
        return "Review Makanan:\n\n" +
                "Menu: " + this.menu + "\n" +
                "Review: " + this.review + "\n" +
                "Tanggal: " + this.date + "\n\n" +
                "Dibagikan dari Aplikasi Review Makanan";
    }

    // METHOD BARU - Override equals dan hashCode untuk comparison yang lebih baik
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Review review = (Review) obj;
        return id != null ? id.equals(review.id) : review.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    // METHOD BARU - ToString untuk debugging
    @Override
    public String toString() {
        return "Review{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", menu='" + menu + '\'' +
                ", isLiked=" + isLiked +
                ", likeCount=" + likeCount +
                ", isBookmarked=" + isBookmarked +
                '}';
    }
}