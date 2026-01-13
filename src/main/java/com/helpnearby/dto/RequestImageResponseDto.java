package com.helpnearby.dto;

import java.util.UUID;

public class RequestImageResponseDto {

	private UUID id;
	private String url;
	private Boolean primaryImage;
	private String s3Key;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Boolean getPrimaryImage() {
		return primaryImage;
	}

	public void setPrimaryImage(Boolean primaryImage) {
		this.primaryImage = primaryImage;
	}

	public String getS3Key() {
		return s3Key;
	}

	public void setS3Key(String s3Key) {
		this.s3Key = s3Key;
	}

}
