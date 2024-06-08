package com.jj.timecapsule;

import java.io.Serializable;

public class TimeCapsule implements Serializable {
    private String id;
    private String title;
    private String textContent;
    private String imageContent;
    private String createdAt;
    private String targetDate;
    private double latitude;
    private double longitude;
    private String userEmail;

    public TimeCapsule(String id, String title, String textContent, String imageContent, String createdAt, String targetDate, double latitude, double longitude, String userEmail) {
        this.id = id;
        this.title = title;
        this.textContent = textContent;
        this.imageContent = imageContent;
        this.createdAt = createdAt;
        this.targetDate = targetDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userEmail = userEmail;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getTextContent() { return textContent; }
    public String getImageContent() { return imageContent; }
    public String getCreatedAt() { return createdAt; }
    public String getTargetDate() { return targetDate; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getUserEmail() { return userEmail; }
}
