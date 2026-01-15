package com.helpnearby.dto;

import jakarta.validation.constraints.NotBlank;

public class FcmTokenDto {
	
	@NotBlank(message = "FCM token is required")
	private String fcmToken;

	public FcmTokenDto() {
	}

	public FcmTokenDto(String fcmToken) {
		this.fcmToken = fcmToken;
	}

	public String getFcmToken() {
		return fcmToken;
	}

	public void setFcmToken(String fcmToken) {
		this.fcmToken = fcmToken;
	}
}
