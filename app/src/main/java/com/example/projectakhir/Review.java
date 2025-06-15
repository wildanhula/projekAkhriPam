// Review.java (versi final dengan Parcelable dan fitur Like/Bookmark/Share)
package com.example.projectakhir;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Review implements Parcelable {
    private String id;
    private String date;
    private String title;
    private String review;
    private String menu;
    private String imagePath;
    private int imageRes;

    private boolean isLiked;
    private int likeCount;
    private boolean isBookmarked;

    public Review() {
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
        this.isLiked = false;
        this.likeCount = 0;
        this.isBookmarked = false;
    }

    public Review(String id, String date, String title, String review, String menu, int imageRes) {
        this(id, date, title, review, menu, null, imageRes);
    }

    protected Review(Parcel in) {
        id = in.readString();
        date = in.readString();
        title = in.readString();
        review = in.readString();
        menu = in.readString();
        imagePath = in.readString();
        imageRes = in.readInt();
        isLiked = in.readByte() != 0;
        likeCount = in.readInt();
        isBookmarked = in.readByte() != 0;
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public String getId() { return id; }
    public String getDate() { return date; }
    public String getTitle() { return title; }
    public String getReview() { return review; }
    public String getMenu() { return menu; }
    public String getImagePath() { return imagePath; }
    public int getImageRes() { return imageRes; }
    public boolean isLiked() { return isLiked; }
    public int getLikeCount() { return likeCount; }
    public boolean isBookmarked() { return isBookmarked; }

    public void setId(String id) { this.id = id; }
    public void setDate(String date) { this.date = date; }
    public void setTitle(String title) { this.title = title; }
    public void setReview(String review) { this.review = review; }
    public void setMenu(String menu) { this.menu = menu; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setImageRes(int imageRes) { this.imageRes = imageRes; }
    public void setLiked(boolean liked) { this.isLiked = liked; }
    public void setLikeCount(int likeCount) { this.likeCount = Math.max(0, likeCount); }
    public void setBookmarked(boolean bookmarked) { this.isBookmarked = bookmarked; }

    public void toggleLike() {
        this.isLiked = !this.isLiked;
        if (this.isLiked) this.likeCount++;
        else this.likeCount = Math.max(0, this.likeCount - 1);
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

    public void updateDate() {
        this.date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    public boolean hasCustomImage() {
        return imagePath != null && !imagePath.trim().isEmpty();
    }

    public String getShareText() {
        return "Review Makanan:\n\n" +
                "Menu: " + this.menu + "\n" +
                "Review: " + this.review + "\n" +
                "Tanggal: " + this.date + "\n\n" +
                "Dibagikan dari Aplikasi Review Makanan";
    }

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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(date);
        dest.writeString(title);
        dest.writeString(review);
        dest.writeString(menu);
        dest.writeString(imagePath);
        dest.writeInt(imageRes);
        dest.writeByte((byte) (isLiked ? 1 : 0));
        dest.writeInt(likeCount);
        dest.writeByte((byte) (isBookmarked ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
