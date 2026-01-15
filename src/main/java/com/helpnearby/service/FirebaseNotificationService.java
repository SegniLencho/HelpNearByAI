package com.helpnearby.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.helpnearby.dto.NotificationResponseDto;
import com.helpnearby.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
	public NotificationResponseDto sendNotificationToUser(String userId, String title, String body, String imageUrl, String data) {
		try {
			String fcmToken = userRepository.findById(userId)
					.map(user -> user.getFcmToken())
					.orElse(null);

			if (fcmToken == null || fcmToken.isEmpty()) {
				logger.warn("No FCM token found for user: {}", userId);
				return new NotificationResponseDto(false, "User does not have a registered FCM token");
			}

			Message.Builder messageBuilder = Message.builder()
					.setToken(fcmToken)
					.setNotification(Notification.builder()
							.setTitle(title)
							.setBody(body)
							.setImage(imageUrl)
							.build());

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
	public NotificationResponseDto sendNotificationToUsers(List<String> userIds, String title, String body, String imageUrl, String data) {
		try {
			List<String> fcmTokens = new ArrayList<>();
			
			for (String userId : userIds) {
				userRepository.findById(userId)
						.map(user -> user.getFcmToken())
						.ifPresent(token -> {
							if (token != null && !token.isEmpty()) {
								fcmTokens.add(token);
							}
						});
			}

			if (fcmTokens.isEmpty()) {
				logger.warn("No FCM tokens found for any of the provided users");
				return new NotificationResponseDto(false, "No valid FCM tokens found for the provided users");
			}

			MulticastMessage.Builder messageBuilder = MulticastMessage.builder()
					.addAllTokens(fcmTokens)
					.setNotification(Notification.builder()
							.setTitle(title)
							.setBody(body)
							.setImage(imageUrl)
							.build());

			if (data != null && !data.isEmpty()) {
				messageBuilder.putData("data", data);
			}

			MulticastMessage multicastMessage = messageBuilder.build();
			@SuppressWarnings("deprecation")
			var batchResponse = firebaseMessaging.sendMulticast(multicastMessage);

			int successCount = batchResponse.getSuccessCount();
			int failureCount = batchResponse.getFailureCount();

			logger.info("Sent notifications to {} users. Success: {}, Failed: {}", 
					fcmTokens.size(), successCount, failureCount);

			String message = String.format("Notifications sent to %d users. Success: %d, Failed: %d", 
					fcmTokens.size(), successCount, failureCount);

			return new NotificationResponseDto(true, message);

		} catch (FirebaseMessagingException e) {
			logger.error("Error sending notifications to users: {}", e.getMessage(), e);
			return new NotificationResponseDto(false, "Failed to send notifications: " + e.getMessage());
		}
	}

	/**
	 * Send notification to all users subscribed to a topic
	 */
	public NotificationResponseDto sendNotificationToTopic(String topic, String title, String body, String imageUrl, String data) {
		try {
			Message.Builder messageBuilder = Message.builder()
					.setTopic(topic)
					.setNotification(Notification.builder()
							.setTitle(title)
							.setBody(body)
							.setImage(imageUrl)
							.build());

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
	 * Send notification directly to an FCM token (for cases where token is already known)
	 */
	public NotificationResponseDto sendNotificationToToken(String fcmToken, String title, String body, String imageUrl, String data) {
		try {
			if (fcmToken == null || fcmToken.isEmpty()) {
				return new NotificationResponseDto(false, "FCM token is required");
			}

			Message.Builder messageBuilder = Message.builder()
					.setToken(fcmToken)
					.setNotification(Notification.builder()
							.setTitle(title)
							.setBody(body)
							.setImage(imageUrl)
							.build());

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
