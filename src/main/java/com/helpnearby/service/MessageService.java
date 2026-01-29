package com.helpnearby.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.helpnearby.dto.MultiUserNotificationRequestDto;
import com.helpnearby.entities.Message;
import com.helpnearby.entities.User;
import com.helpnearby.repository.MessageRepository;
import com.helpnearby.repository.UserRepository;

@Service
public class MessageService {

	private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

	private MessageRepository messageRepository;

	private UserRepository userRepository;

	private NotificationService notificationService;

	public MessageService(MessageRepository messageRepository, UserRepository userRepository,
			NotificationService notificationService) {
		this.messageRepository = messageRepository;
		this.userRepository = userRepository;
		this.notificationService = notificationService;
	}

	public Message createMessage(Message message) {
		if (message.getTimestamp() == null) {
			message.setTimestamp(LocalDateTime.now());
		}
		return messageRepository.save(message);
	}

	public List<Message> getConversationBetweenUsers(String userId1, String userId2) {
		return messageRepository.findConversationBetweenUsers(userId1, userId2);
	}

	public List<Message> getUnreadMessages(String userId) {
		return messageRepository.findUnreadMessagesByReceiverId(userId);
	}

	public List<String> getUserConversationPartners(String userId) {
		return messageRepository.findUserConversationPartners(userId);
	}

	@Transactional
	public void markMessagesAsRead(String senderId, String receiverId) {
		List<Message> unreadMessages = messageRepository.findUnreadMessagesByReceiverId(receiverId);
		unreadMessages.stream().filter(msg -> msg.getSenderId().equals(senderId)).forEach(msg -> {
			msg.setIsRead(true);
			messageRepository.save(msg);
		});
	}

	public Optional<Message> getMessageById(String id) {
		return messageRepository.findById(id);
	}

	@Async
	public void sendNotfication(Message message, String senderId, String receiverId) {
		logger.info("sendNotification");
		logger.debug("sendNotification: {}", message);
		List<User> users = userRepository.findAllById(List.of(receiverId, senderId));
		Optional<User> receiver = users.stream().filter(u -> u.getId().equals(receiverId)).findFirst();
		Optional<User> sender = users.stream().filter(u -> u.getId().equals(senderId)).findFirst();
		if (receiver.isPresent() && sender.isPresent()) {
			logger.info("sender and receiver details found ");
			MultiUserNotificationRequestDto userNotification = new MultiUserNotificationRequestDto();
			// Notify Only receiver
			List<User> userReceiveNotfication = new ArrayList<>();
			userReceiveNotfication.add(receiver.get());
			userNotification.setUserIds(userReceiveNotfication);
			userNotification.setTitle("New Message from " + sender.get().getName());
			userNotification.setBody(message.getContent());
			Map<String, String> dataMap = new HashMap<>();
			// Send RequestId so that user can see request details once clicked on
			// Notification
			dataMap.put("type", "NEW_MESSAGE");
			dataMap.put("senderId", sender.get().getId());
			dataMap.put("receiverId", receiver.get().getId());
			dataMap.put("senderName", sender.get().getName());
			userNotification.setData(dataMap);
			notificationService.sendNotificationToUsers(userNotification);
			logger.info("Notification sent successfully " + userNotification.toString());
			logger.debug("Incoming request payload: {}", userNotification.toString());

		}
	}
}
