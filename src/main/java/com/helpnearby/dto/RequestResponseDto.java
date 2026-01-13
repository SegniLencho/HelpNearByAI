package com.helpnearby.dto;

import java.time.Instant;
import java.util.List;

public class RequestResponseDto {

    private String id;
    private String userId;

    private String title;
    private String description;
    private String category;
    private Double reward;

    private Double latitude;
    private Double longitude;

    private String status;

    private List<RequestImageResponseDto> images;

    private String urgency;

    private Instant createdAt;
    private Instant updatedAt;

    /**
     * Raw geometry string (WKB hex) – optional to expose
     * You may later remove this from API if frontend doesn't need it
     */
    private String location;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<RequestImageResponseDto> getImages() {
		return images;
	}

	public void setImages(List<RequestImageResponseDto> images) {
		this.images = images;
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

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

    
    
    // getters & setters
}
