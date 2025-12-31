package com.helpnearby.dto;

import java.time.Instant;
import java.util.UUID;

public class RequestFeedDTO {
    private UUID id;
    private String title;
    private String category;
    private Double reward;
    private String status;
    private String urgency;
    private Instant createdAt;
    private Double distanceMiles;
    private String thumbnailUrl;

    // ✅ Constructor (needed for JPQL "new DTO(...)")
    public RequestFeedDTO(UUID id, String title, String category, Double reward,
                          String status, String urgency, Instant createdAt,
                          Double distanceMiles, String thumbnailUrl) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.reward = reward;
        this.status = status;
        this.urgency = urgency;
        this.createdAt = createdAt;
        this.distanceMiles = distanceMiles;
        this.thumbnailUrl = thumbnailUrl;
    }

    // ✅ Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getReward() {
        return reward;
    }

    public void setReward(Double reward) {
        this.reward = reward;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Double getDistanceMiles() {
        return distanceMiles;
    }

    public void setDistanceMiles(Double distanceMiles) {
        this.distanceMiles = distanceMiles;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
