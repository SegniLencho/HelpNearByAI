package com.helpnearby.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import com.google.firebase.messaging.BatchResponse;
import com.helpnearby.dto.MultiUserNotificationRequestDto;
import com.helpnearby.dto.NotificationResponseDto;
import com.helpnearby.entities.User;
import com.helpnearby.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FirebaseNotificationService {

	private static final Logger logger = LoggerFactory.getLogger(FirebaseNotificationService.class);

	@Autowired
	private FirebaseMessaging firebaseMessaging;

	@Autowired
	private UserRepository userRepository;

	/**
	 * Send notification to a single user by user ID
	 */
	public NotificationResponseDto sendNotificationToUser(String userId, String title, String body, String imageUrl,
			String data) {
		try {
			String fcmToken = userRepository.findById(userId).map(user -> user.getFcmToken()).orElse(null);

			if (fcmToken == null || fcmToken.isEmpty()) {
				logger.warn("No FCM token found for user: {}", userId);
				return new NotificationResponseDto(false, "User does not have a registered FCM token");
			}

			Notification.Builder notificationBuilder = Notification.builder().setTitle(title).setBody(body);

			// Only set image if it's not null or empty
			if (imageUrl != null && !imageUrl.trim().isEmpty()) {
				notificationBuilder.setImage(imageUrl);
			}

			Message.Builder messageBuilder = Message.builder().setToken(fcmToken)
					.setNotification(notificationBuilder.build());

			if (data != null && !data.isEmpty()) {
				messageBuilder.putData("data", data);
			}

			Message message = messageBuilder.build();
			String response = firebaseMessaging.send(message);

			logger.info("Successfully sent notification to user {}: {}", userId, response);
			return new NotificationResponseDto(true, "Notification sent successfully", response);

		} catch (FirebaseMessagingException e) {
			logger.error("Error sending notification to user {}: {}", userId, e.getMessage(), e);
			return new NotificationResponseDto(false, "Failed to send notification: " + e.getMessage());
		}
	}

	/**
	 * Send notification to multiple users by user IDs
	 */
	public NotificationResponseDto sendNotificationToUsers(MultiUserNotificationRequestDto dto) {		

		try {
			List<User> users = userRepository.findAllById(dto.getUserIds());
			List<String> fcmTokens = new ArrayList<>();
			List<String> usersWithoutTokens = new ArrayList<>();

			for (User user : users) {
				if (user.getFcmToken() != null && !user.getFcmToken().isBlank()) {
					fcmTokens.add(user.getFcmToken());
				} else {
					usersWithoutTokens.add(user.getId());
				}
			}
			if (fcmTokens.isEmpty()) {
				return new NotificationResponseDto(false, "No valid FCM tokens found for provided users");
			}

			Notification.Builder notificationBuilder = Notification.builder().setTitle(dto.getTitle()).setBody(dto.getBody());

			if (dto.getImageUrl() != null && !dto.getImageUrl().isBlank()) {
				notificationBuilder.setImage(dto.getImageUrl());
			}

			Notification notification = notificationBuilder.build();

			MulticastMessage.Builder multicastBuilder = MulticastMessage.builder().addAllTokens(fcmTokens)
					.setNotification(notification);

			if (dto.getData() != null && !dto.getData().isEmpty()) {
				multicastBuilder.putAllData(dto.getData());
			}

			MulticastMessage multicastMessage = multicastBuilder.build();

			// ✅ NON-DEPRECATED API (FIXES /batch 404)
			BatchResponse response = firebaseMessaging.sendEachForMulticast(multicastMessage);

			int successCount = response.getSuccessCount();
			int failureCount = response.getFailureCount();

			List<String> invalidTokens = new ArrayList<>();

			for (int i = 0; i < response.getResponses().size(); i++) {
				SendResponse sendResponse = response.getResponses().get(i);

				if (!sendResponse.isSuccessful()) {
					FirebaseMessagingException ex = sendResponse.getException();
					String token = fcmTokens.get(i);

					logger.warn("Failed to send to token {}: {}", token, ex.getMessage());

					if (isInvalidToken(ex)) {
						invalidTokens.add(token);
					}
				}
			}

			if (!invalidTokens.isEmpty()) {
				userRepository.clearFcmTokens(invalidTokens);
				logger.info("Removed {} invalid FCM tokens", invalidTokens.size());
			}

			return new NotificationResponseDto(true,
					String.format("Notifications sent. Success: %d, Failed: %d", successCount, failureCount));

		} catch (Exception e) {
			logger.error("Notification sending failed", e);
			return new NotificationResponseDto(false, "Failed to send notifications: " + e.getMessage());
		}
	}

	private boolean isInvalidToken(FirebaseMessagingException ex) {
		return ex.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED
				|| ex.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT;
	}

	/**
	 * Send notification to all users subscribed to a topic
	 */
	public NotificationResponseDto sendNotificationToTopic(String topic, String title, String body, String imageUrl,
			String data) {
		try {
			Notification.Builder notificationBuilder = Notification.builder().setTitle(title).setBody(body);

			// Only set image if it's not null or empty
			if (imageUrl != null && !imageUrl.trim().isEmpty()) {
				notificationBuilder.setImage(imageUrl);
			}

			Message.Builder messageBuilder = Message.builder().setTopic(topic)
					.setNotification(notificationBuilder.build());

			if (data != null && !data.isEmpty()) {
				messageBuilder.putData("data", data);
			}

			Message message = messageBuilder.build();
			String response = firebaseMessaging.send(message);

			logger.info("Successfully sent notification to topic {}: {}", topic, response);
			return new NotificationResponseDto(true, "Notification sent successfully to topic", response);

		} catch (FirebaseMessagingException e) {
			logger.error("Error sending notification to topic {}: {}", topic, e.getMessage(), e);
			return new NotificationResponseDto(false, "Failed to send notification: " + e.getMessage());
		}
	}

	/**
	 * Send notification directly to an FCM token (for cases where token is already
	 * known)
	 */
	public NotificationResponseDto sendNotificationToToken(String fcmToken, String title, String body, String imageUrl,
			String data) {
		try {
			if (fcmToken == null || fcmToken.isEmpty()) {
				return new NotificationResponseDto(false, "FCM token is required");
			}

			Notification.Builder notificationBuilder = Notification.builder().setTitle(title).setBody(body);

			// Only set image if it's not null or empty
			if (imageUrl != null && !imageUrl.trim().isEmpty()) {
				notificationBuilder.setImage(imageUrl);
			}

			Message.Builder messageBuilder = Message.builder().setToken(fcmToken)
					.setNotification(notificationBuilder.build());

			if (data != null && !data.isEmpty()) {
				messageBuilder.putData("data", data);
			}

			Message message = messageBuilder.build();
			String response = firebaseMessaging.send(message);

			logger.info("Successfully sent notification to token: {}", response);
			return new NotificationResponseDto(true, "Notification sent successfully", response);

		} catch (FirebaseMessagingException e) {
			logger.error("Error sending notification to token: {}", e.getMessage(), e);
			return new NotificationResponseDto(false, "Failed to send notification: " + e.getMessage());
		}
	}

	/**
	 * Subscribe a user's FCM token to a topic
	 */
	public NotificationResponseDto subscribeToTopic(String fcmToken, String topic) {
		try {
			if (fcmToken == null || fcmToken.isEmpty()) {
				return new NotificationResponseDto(false, "FCM token is required");
			}

			var response = firebaseMessaging.subscribeToTopic(List.of(fcmToken), topic);

			if (response.getSuccessCount() > 0) {
				logger.info("Successfully subscribed token to topic: {}", topic);
				return new NotificationResponseDto(true, "Successfully subscribed to topic");
			} else {
				logger.warn("Failed to subscribe token to topic: {}", topic);
				return new NotificationResponseDto(false, "Failed to subscribe to topic");
			}

		} catch (FirebaseMessagingException e) {
			logger.error("Error subscribing to topic {}: {}", topic, e.getMessage(), e);
			return new NotificationResponseDto(false, "Failed to subscribe: " + e.getMessage());
		}
	}

	/**
	 * Unsubscribe a user's FCM token from a topic
	 */
	public NotificationResponseDto unsubscribeFromTopic(String fcmToken, String topic) {
		try {
			if (fcmToken == null || fcmToken.isEmpty()) {
				return new NotificationResponseDto(false, "FCM token is required");
			}

			var response = firebaseMessaging.unsubscribeFromTopic(List.of(fcmToken), topic);

			if (response.getSuccessCount() > 0) {
				logger.info("Successfully unsubscribed token from topic: {}", topic);
				return new NotificationResponseDto(true, "Successfully unsubscribed from topic");
			} else {
				logger.warn("Failed to unsubscribe token from topic: {}", topic);
				return new NotificationResponseDto(false, "Failed to unsubscribe from topic");
			}

		} catch (FirebaseMessagingException e) {
			logger.error("Error unsubscribing from topic {}: {}", topic, e.getMessage(), e);
			return new NotificationResponseDto(false, "Failed to unsubscribe: " + e.getMessage());
		}
	}

}
