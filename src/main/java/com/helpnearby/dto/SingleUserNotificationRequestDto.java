package com.helpnearby.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Map;

import com.helpnearby.entities.User;

public class SingleUserNotificationRequestDto {

	@NotEmpty(message = "User IDs list is required")
	private User user;

	@NotBlank(message = "Title is required")
	private String title;

	@NotBlank(message = "Body is required")
	private String body;

	private String imageUrl;

	Map<String, String> data;

	public SingleUserNotificationRequestDto() {
	}

	public SingleUserNotificationRequestDto(User user, String title, String body) {
		this.user = user;
		this.title = title;
		this.body = body;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}
}
