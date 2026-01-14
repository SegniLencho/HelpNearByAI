package com.helpnearby.dto;

import java.util.List;

public class UpdateRequestDto {
	private String requestId;
	private String title;
	private String description;
	private String category;
	private Double reward;
	private String urgency;
	private String status;
	private List<UpdateRequestImageDto> removedImages;
	private List<UpdateRequestImageDto> newImages;

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

	public String getUrgency() {
		return urgency;
	}

	public void setUrgency(String urgency) {
		this.urgency = urgency;
	}

	public List<UpdateRequestImageDto> getRemovedImages() {
		return removedImages;
	}

	public void setRemovedImages(List<UpdateRequestImageDto> removedImageIds) {
		this.removedImages = removedImageIds;
	}

	public List<UpdateRequestImageDto> getNewImages() {
		return newImages;
	}

	public void setNewImages(List<UpdateRequestImageDto> newImages) {
		this.newImages = newImages;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}