package com.helpnearby.dto;

import jakarta.validation.constraints.NotBlank;

public class NotificationRequestDto {
	
	@NotBlank(message = "User ID is required")
	private String userId;
	
	@NotBlank(message = "Title is required")
	private String title;
	
	@NotBlank(message = "Body is required")
	private String body;
	
	private String imageUrl;
	private String data; // JSON string for additional data

	public NotificationRequestDto() {
	}

	public NotificationRequestDto(String userId, String title, String body) {
		this.userId = userId;
		this.title = title;
		this.body = body;
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

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
