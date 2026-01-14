package com.helpnearby.dto;

import java.util.Objects;
import java.util.UUID;

public class RequestImageDto {
	private UUID id;
	private String url;
    private boolean primaryImage;
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

	@Override
	public int hashCode() {
		return Objects.hash(id, url);
	}

	public boolean isPrimaryImage() {
		return primaryImage;
	}

	public void setPrimaryImage(boolean primaryImage) {
		this.primaryImage = primaryImage;
	}

	public String getS3Key() {
		return s3Key;
	}

	public void setS3Key(String s3Key) {
		this.s3Key = s3Key;
	}

	@Override
	public String toString() {
		return "RequestImageDto [id=" + id + ", url=" + url + ", primaryImage=" + primaryImage + ", s3Key=" + s3Key
				+ ", getId()=" + getId() + ", getUrl()=" + getUrl() + ", hashCode()=" + hashCode()
				+ ", isPrimaryImage()=" + isPrimaryImage() + ", getS3Key()=" + getS3Key() + ", getClass()=" + getClass()
				+ ", toString()=" + super.toString() + "]";
	}




}
