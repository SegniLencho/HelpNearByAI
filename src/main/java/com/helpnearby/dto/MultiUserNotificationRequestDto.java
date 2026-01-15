package com.helpnearby.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class MultiUserNotificationRequestDto {
	
	@NotEmpty(message = "User IDs list is required")
	private List<String> userIds;
	
	@NotBlank(message = "Title is required")
	private String title;
	
	@NotBlank(message = "Body is required")
	private String body;
	
	private String imageUrl;
	private String data; // JSON string for additional data

	public MultiUserNotificationRequestDto() {
	}

	public MultiUserNotificationRequestDto(List<String> userIds, String title, String body) {
		this.userIds = userIds;
		this.title = title;
		this.body = body;
	}

	public List<String> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
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
