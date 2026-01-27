package com.helpnearby.dto;

import jakarta.validation.constraints.NotBlank;

public class TopicNotificationRequestDto {
	
	@NotBlank(message = "Topic is required")
	private String topic;
	
	@NotBlank(message = "Title is required")
	private String title;
	
	@NotBlank(message = "Body is required")
	private String body;
	
	private String imageUrl;
	private String data; // JSON string for additional data

	public TopicNotificationRequestDto() {
	}

	public TopicNotificationRequestDto(String topic, String title, String body) {
		this.topic = topic;
		this.title = title;
		this.body = body;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
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
