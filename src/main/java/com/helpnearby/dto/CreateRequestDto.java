package com.helpnearby.dto;

import java.util.List;

public class CreateRequestDto {
	
	private String title;
    private String description;
    private String category;
    private Double reward;
    private double latitude;
    private double longitude;
    private String urgency;
    private List<CreateRequestImageDto> images;

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
	public String getUrgency() {
		return urgency;
	}
	public void setUrgency(String urgency) {
		this.urgency = urgency;
	}
	public List<CreateRequestImageDto> getImages() {
		return images;
	}
	public void setImages(List<CreateRequestImageDto> images) {
		this.images = images;
	}
	
}