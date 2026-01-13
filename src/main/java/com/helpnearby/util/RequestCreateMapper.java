package com.helpnearby.util;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.helpnearby.dto.CreateRequestDto;
import com.helpnearby.dto.CreateRequestImageDto;
import com.helpnearby.entities.Request;
import com.helpnearby.entities.RequestImage;

public class RequestCreateMapper {

	public static Request toEntity(String userId,CreateRequestDto dto) {

		Request request = new Request();
		request.setId(UUID.randomUUID().toString());
		request.setUserId(userId);

		request.setTitle(dto.getTitle());
		request.setDescription(dto.getDescription());
		request.setCategory(dto.getCategory());
		request.setReward(dto.getReward());

		request.setLatitude(dto.getLatitude());
		request.setLongitude(dto.getLongitude());

		request.setUrgency(dto.getUrgency());
		request.setStatus("OPEN");

		Instant now = Instant.now();
		request.setCreatedAt(now);
		request.setUpdatedAt(now);

		if (dto.getImages() != null && !dto.getImages().isEmpty()) {
			List<RequestImage> images = dto.getImages().stream().map(imgDto -> toImageEntity(imgDto, request))
					.collect(Collectors.toList());

			request.setImages(images);
		}

		return request;
	}

	private static RequestImage toImageEntity(CreateRequestImageDto dto, Request request) {

		RequestImage image = new RequestImage();
		image.setUrl(dto.getUrl());
		image.setPrimaryImage(dto.isPrimaryImage());
		image.setS3Key(dto.getS3Key());
		image.setRequest(request); 
		return image;
	}
}
