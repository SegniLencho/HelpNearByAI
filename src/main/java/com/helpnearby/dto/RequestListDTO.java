package com.helpnearby.dto;

import java.time.Instant;

public record RequestListDTO(
		String id, String title, String description,String category, Double reward, String urgency,Double latitude,Double longitude, Instant createdAt,
		String images) {
}