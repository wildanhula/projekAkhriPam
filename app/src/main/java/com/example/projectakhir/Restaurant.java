package com.example.projectakhir;

public class Restaurant {
    private String name;
    private String description;
    private int imageResId;
    private float rating;

    public Restaurant(String name, String description, int imageResId, float rating) {
        this.name = name;
        this.description = description;
        this.imageResId = imageResId;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResId() {
        return imageResId;
    }

    public float getRating() {
        return rating;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
