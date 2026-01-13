package com.helpnearby.dto;

import java.util.Objects;
import java.util.UUID;

public class RequestImageDto {
	private UUID id;
	private String url;

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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RequestImageDto other = (RequestImageDto) obj;
		return Objects.equals(id, other.id) && Objects.equals(url, other.url);
	}

	@Override
	public String toString() {
		return "RequestImageDto [id=" + id + ", url=" + url + "]";
	}


}
