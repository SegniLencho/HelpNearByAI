package com.helpnearby.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "requests", indexes = { @Index(name = "idx_user_id", columnList = "user_id"),
		@Index(name = "idx_created_at", columnList = "created_at"), @Index(name = "idx_status", columnList = "status"),
		@Index(name = "idx_status_created_at", columnList = "status, created_at") })
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Request {

	@Id
	@Column(length = 36)
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

	// OPEN, INPROGRESS, CLOSED
	private String status;

	@JsonManagedReference
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "request_id")
	@BatchSize(size = 20) // Load images in batches
	private List<RequestImage> images;

	// LOW, MEDIUM, URGENT
	private String urgency;

	@Column(nullable = false)
	private Instant updatedAt;

	/**
	 * ALWAYS stored in UTC
	 */
	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	@Column(columnDefinition = "geography(Point,4326)", insertable = false, updatable = false)
	private String location;

	public Request() {
		this.id = UUID.randomUUID().toString();
	}

	/**
	 * Ensures UTC timestamp is set automatically
	 */
	@PrePersist
	protected void onCreate() {
		Instant now = Instant.now();
		this.createdAt = now; // UTC
		this.updatedAt = now; // same on creation
		if (this.status == null)
			this.status = "OPEN";
		if (this.urgency == null)
			this.urgency = "MEDIUM";
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = Instant.now(); // UTC
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

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public String getLocation() {
		return location;
	}

	public List<RequestImage> getImages() {
		return images;
	}

	public void setImages(List<RequestImage> images) {
		this.images = images;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
