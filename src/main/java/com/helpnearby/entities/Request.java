package com.helpnearby.entities;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.*;
import java.util.UUID;

import ch.qos.logback.classic.Logger;

@Entity
@Table(name = "requests")
public class Request {

    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId; // Request owner

    private String title;

    @Column(length = 2000)
    private String description;

    private String category;

    private Double reward;

    private double latitude;

    private double longitude;
    
    private String status;//OPEN,INPROGRESS,CLOSED

    
    @ElementCollection
    @CollectionTable(name = "request_images", joinColumns = @JoinColumn(name = "request_id"))
    @Column(name = "image_urls")
    private List<String> imageUrls;

    private String urgency; // MEDIUM, LOW, URGENT

    private LocalDateTime createdAt;

    public Request() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.urgency = "OPEN";
    }

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

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public List<String> getImageUrls() {
		return imageUrls;
	}

	public void setImageUrls(List<String> imageUrls) {
		this.imageUrls = imageUrls;
	}

	public String getUrgency() {
		return urgency;
	}

	public void setUrgency(String urgency) {
		this.urgency = urgency;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

    
}
