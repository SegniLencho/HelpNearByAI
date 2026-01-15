package com.helpnearby.controller;

import com.helpnearby.dto.MultiUserNotificationRequestDto;
import com.helpnearby.dto.NotificationRequestDto;
import com.helpnearby.dto.NotificationResponseDto;
import com.helpnearby.dto.TopicNotificationRequestDto;
import com.helpnearby.service.FirebaseNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

	private final FirebaseNotificationService notificationService;

	public NotificationController(FirebaseNotificationService notificationService) {
		this.notificationService = notificationService;
	}

	/**
	 * Send notification to a single user
	 * POST /api/notifications/user
	 */
	@PostMapping("/user")
	public ResponseEntity<NotificationResponseDto> sendToUser(@Valid @RequestBody NotificationRequestDto request) {
		NotificationResponseDto response = notificationService.sendNotificationToUser(
				request.getUserId(),
				request.getTitle(),
				request.getBody(),
				request.getImageUrl(),
				request.getData()
		);
		return ResponseEntity.ok(response);
	}

	/**
	 * Send notification to multiple users
	 * POST /api/notifications/users
	 */
	@PostMapping("/users")
	public ResponseEntity<NotificationResponseDto> sendToUsers(@Valid @RequestBody MultiUserNotificationRequestDto request) {
		NotificationResponseDto response = notificationService.sendNotificationToUsers(
				request.getUserIds(),
				request.getTitle(),
				request.getBody(),
				request.getImageUrl(),
				request.getData()
		);
		return ResponseEntity.ok(response);
	}

	/**
	 * Send notification to a topic
	 * POST /api/notifications/topic
	 */
	@PostMapping("/topic")
	public ResponseEntity<NotificationResponseDto> sendToTopic(@Valid @RequestBody TopicNotificationRequestDto request) {
		NotificationResponseDto response = notificationService.sendNotificationToTopic(
				request.getTopic(),
				request.getTitle(),
				request.getBody(),
				request.getImageUrl(),
				request.getData()
		);
		return ResponseEntity.ok(response);
	}
}
