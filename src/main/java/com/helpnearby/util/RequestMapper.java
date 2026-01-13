package com.helpnearby.util;

import java.util.stream.Collectors;

import com.helpnearby.dto.RequestDto;
import com.helpnearby.dto.RequestImageDto;
import com.helpnearby.entities.Request;
import com.helpnearby.entities.RequestImage;

public class RequestMapper {

    public static RequestDto toDto(Request request) {
        if (request == null) return null;

        RequestDto dto = new RequestDto();
        dto.setId(request.getId());
        dto.setUserId(request.getUserId());
        dto.setTitle(request.getTitle());
        dto.setDescription(request.getDescription());
        dto.setCategory(request.getCategory());
        dto.setReward(request.getReward());
        dto.setLatitude(request.getLatitude());
        dto.setLongitude(request.getLongitude());
        dto.setStatus(request.getStatus());
        dto.setUrgency(request.getUrgency());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setUpdatedAt(request.getUpdatedAt());

        if (request.getImages() != null) {
            dto.setImages(
                request.getImages()
                    .stream()
                    .map(RequestMapper::toImageDto)
                    .collect(Collectors.toList())
            );
        }

        return dto;
    }

    private static RequestImageDto toImageDto(RequestImage image) {
        RequestImageDto dto = new RequestImageDto();
        dto.setId(image.getId());
        dto.setUrl(image.getUrl());
        dto.setPrimaryImage(image.isPrimaryImage());
        dto.setS3Key(image.getS3Key());
        return dto;
    }
}
