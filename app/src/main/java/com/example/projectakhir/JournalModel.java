// JournalModel.java
package com.example.projectakhir;

public class JournalModel {
    private String key; // Untuk ID unik dari Firebase
    private String image; // Untuk menyimpan data gambar Base64
    private String title;
    private String description;

    // Constructor kosong wajib untuk Firebase
    public JournalModel() {
    }

    public JournalModel(String image, String title, String description) {
        this.image = image;
        this.title = title;
        this.description = description;
    }

    // Getters
    public String getKey() {
        return key;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    // Setters
    public void setKey(String key) {
        this.key = key;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}