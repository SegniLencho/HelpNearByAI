package com.helpnearby.dto;

import java.time.Instant;
import java.util.List;

public class RequestDto {

	    private String id;
	    private String userId;

	    private String title;
	    private String description;
	    private String category;

	    private Double reward;

	    private double latitude;
	    private double longitude;

	    // OPEN, INPROGRESS, CLOSED
	    private String status;

	    // LOW, MEDIUM, URGENT
	    private String urgency;

	    private Instant createdAt;
	    private Instant updatedAt;

	    private List<RequestImageDto> images;

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

		public void setCreatedAt(Instant createdAt) {
			this.createdAt = createdAt;
		}

		public Instant getUpdatedAt() {
			return updatedAt;
		}

		public void setUpdatedAt(Instant updatedAt) {
			this.updatedAt = updatedAt;
		}

		public List<RequestImageDto> getImages() {
			return images;
		}

		public void setImages(List<RequestImageDto> images) {
			this.images = images;
		
	}
}
