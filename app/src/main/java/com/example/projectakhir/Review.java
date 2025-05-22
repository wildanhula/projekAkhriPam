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
    private int imageRes;

    public Review(String id, String date, String title, String review, String menu, int imageRes) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.review = review;
        this.menu = menu;
        this.imageRes = imageRes;
    }

    // Getters
    public String getId() { return id; }
    public String getDate() { return date; }
    public String getTitle() { return title; }
    public String getReview() { return review; }
    public String getMenu() { return menu; }
    public int getImageRes() { return imageRes; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setReview(String review) { this.review = review; }
    public void setMenu(String menu) { this.menu = menu; }
    public void updateDate() {
        this.date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }
}