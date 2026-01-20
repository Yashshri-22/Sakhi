package com.example.sakhi;

public class HospitalModel {
    private String name;
    private String distance;
    private boolean isVerified;
    private String type;
    private String description; // New: For Wikipedia Text
    private int imageResId;     // New: For the specific photo

    public HospitalModel(String name, String distance, boolean isVerified, String type, String description, int imageResId) {
        this.name = name;
        this.distance = distance;
        this.isVerified = isVerified;
        this.type = type;
        this.description = description;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public String getDistance() { return distance; }
    public boolean isVerified() { return isVerified; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public int getImageResId() { return imageResId; }
}
