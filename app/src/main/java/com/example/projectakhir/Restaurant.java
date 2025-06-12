package com.example.projectakhir;

public class Restaurant {
    private String id;
    private String name;
    private String description;
    private String location;
    private String price;
    private String openingHours;
    private String imageUrl;

    // Constructor kosong (dibutuhkan oleh Firebase)
    public Restaurant() {}

    // Constructor lengkap
    public Restaurant(String id, String name, String description, String location, String price, String openingHours, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.price = price;
        this.openingHours = openingHours;
        this.imageUrl = imageUrl;
    }

    // Getter
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getPrice() {
        return price;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // Setter
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
