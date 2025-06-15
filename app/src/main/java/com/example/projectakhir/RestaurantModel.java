package com.example.projectakhir;

import java.io.Serializable;

public class RestaurantModel implements Serializable {
    private String id;
    private String imageUrl;
    private String name;
    private String description;
    private String location;
    private String price;
    private String openingHours;

    // Diperlukan untuk Firebase
    public RestaurantModel() {
    }

    public RestaurantModel(String id, String imageUrl, String name, String description,
                           String location, String price, String openingHours) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.description = description;
        this.location = location;
        this.price = price;
        this.openingHours = openingHours;
    }

    // Getter dan Setter
    public String getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
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

    public void setId(String id) {
        this.id = id;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
}
