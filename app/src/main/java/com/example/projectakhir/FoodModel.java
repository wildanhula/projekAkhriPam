package com.example.projectakhir;

public class FoodModel {
    private String imageUri;
    private String title;
    private String description;
    private String location;
    private String price;
    private String openingHours;

    public FoodModel() {} // Wajib untuk Firebase

    public FoodModel(String imageUri, String title, String description,
                     String location, String price, String openingHours) {
        this.imageUri = imageUri;
        this.title = title;
        this.description = description;
        this.location = location;
        this.price = price;
        this.openingHours = openingHours;
    }

    // Getter
    public String getImageUri() { return imageUri; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getPrice() { return price; }
    public String getOpeningHours() { return openingHours; }

    // Setter
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setLocation(String location) { this.location = location; }
    public void setPrice(String price) { this.price = price; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }
}
