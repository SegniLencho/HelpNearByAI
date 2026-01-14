package com.helpnearby.dto;

public class CreateRequestImageDto {
	private String url;
	private boolean primaryImage;
	private String s3Key;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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
		return "CreateRequestImageDTO [url=" + url + ", primaryImage=" + primaryImage + ", s3Key=" + s3Key + "]";
	}

}
