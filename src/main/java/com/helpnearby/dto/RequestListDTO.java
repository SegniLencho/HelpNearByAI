package com.helpnearby.dto;

import java.time.Instant;
import java.util.UUID;

public record RequestListDTO(
		String id, String title, String description,String category, Double reward, String urgency,Double latitude,Double longitude, Instant createdAt,
		String images,UUID imageId,String s3Key) {
}