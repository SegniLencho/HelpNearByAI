package com.helpnearby.util;

import com.helpnearby.dto.RequestDto;
import com.helpnearby.dto.RequestImageDto;
import com.helpnearby.entities.Request;

public class RequestToDtoConvertor {

	public RequestToDtoConvertor() {

	}

	public static RequestDto toDto(Request request) {
		RequestDto dto = new RequestDto();

		dto.setId(request.getId());
		dto.setTitle(request.getTitle());
		dto.setDescription(request.getDescription());
		dto.setCategory(request.getCategory());
		dto.setReward(request.getReward());
		dto.setStatus(request.getStatus());

		dto.setImages(request.getImages().stream().map(img -> {
			RequestImageDto i = new RequestImageDto();
			i.setId(img.getId());
			i.setUrl(img.getUrl());
			return i;
		}).toList());

		return dto;
	}

}
