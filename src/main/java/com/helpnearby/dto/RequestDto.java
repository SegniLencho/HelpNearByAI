package com.helpnearby.dto;

import java.util.List;

public class RequestDto {

	private String id;
	private String title;
	private String description;
	private String category;
	private Double reward;
	private String status;

	private List<RequestImageDto> images;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<RequestImageDto> getImages() {
		return images;
	}

	public void setImages(List<RequestImageDto> images) {
		this.images = images;
	}

	@Override
	public String toString() {
		return "RequestDto [id=" + id + ", title=" + title + ", description=" + description + ", category=" + category
				+ ", reward=" + reward + ", status=" + status + ", images=" + images + "]";
	}

}
