package com.helpnearby.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.helpnearby.dto.MessageDto;
import com.helpnearby.entities.Message;
import com.helpnearby.service.MessageService;
import com.helpnearby.service.NotificationService;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
	private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

	@Autowired
	private MessageService messageService;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	// WebSocket endpoint for sending messages
	@MessageMapping("/chat.send")
	public void sendMessage(@Payload MessageDto messageDto) {
		// Save message to database
		Message message = new Message(messageDto.getSenderId(), messageDto.getReceiverId(), messageDto.getContent());
		Message savedMessage = messageService.createMessage(message);

		// Convert to DTO
		MessageDto responseDto = new MessageDto(savedMessage.getId(), savedMessage.getSenderId(),
				savedMessage.getReceiverId(), savedMessage.getContent(), savedMessage.getTimestamp(),
				savedMessage.getIsRead());

		// Send to sender's personal queue (user-specific destination)
		messagingTemplate.convertAndSendToUser(messageDto.getSenderId(), "/queue/messages", responseDto);

		// Send to receiver's personal queue (user-specific destination)
		messagingTemplate.convertAndSendToUser(messageDto.getReceiverId(), "/queue/messages", responseDto);
		
		logger.info("send Notification from chat.send");
		messageService.sendNotfication(message, savedMessage.getSenderId(), savedMessage.getReceiverId());

	}

	// REST endpoint to get conversation between two users
	@GetMapping("/conversation/{userId1}/{userId2}")
	public ResponseEntity<List<MessageDto>> getConversation(@PathVariable String userId1,
			@PathVariable String userId2) {
		List<Message> messages = messageService.getConversationBetweenUsers(userId1, userId2);
		List<MessageDto> messageDtos = messages.stream().map(msg -> new MessageDto(msg.getId(), msg.getSenderId(),
				msg.getReceiverId(), msg.getContent(), msg.getTimestamp(), msg.getIsRead()))
				.collect(Collectors.toList());
		return ResponseEntity.ok(messageDtos);
	}

	// REST endpoint to get unread messages for a user
	@GetMapping("/unread/{userId}")
	public ResponseEntity<List<MessageDto>> getUnreadMessages(@PathVariable String userId) {
		List<Message> messages = messageService.getUnreadMessages(userId);
		List<MessageDto> messageDtos = messages.stream().map(msg -> new MessageDto(msg.getId(), msg.getSenderId(),
				msg.getReceiverId(), msg.getContent(), msg.getTimestamp(), msg.getIsRead()))
				.collect(Collectors.toList());
		return ResponseEntity.ok(messageDtos);
	}

	// REST endpoint to get all conversation partners for a user
	@GetMapping("/conversations/{userId}")
	public ResponseEntity<List<String>> getConversationPartners(@PathVariable String userId) {
		List<String> partners = messageService.getUserConversationPartners(userId);
		return ResponseEntity.ok(partners);
	}

	// REST endpoint to mark messages as read
	@PostMapping("/mark-read/{senderId}/{receiverId}")
	public ResponseEntity<Void> markMessagesAsRead(@PathVariable String senderId, @PathVariable String receiverId) {
		messageService.markMessagesAsRead(senderId, receiverId);
		return ResponseEntity.ok().build();
	}

	// REST endpoint to send a message (alternative to WebSocket)
	@PostMapping("/send")
	public ResponseEntity<MessageDto> sendMessageRest(@RequestBody MessageDto messageDto) {
		logger.debug("/send endpoint called");
		Message message = new Message(messageDto.getSenderId(), messageDto.getReceiverId(), messageDto.getContent());
		Message savedMessage = messageService.createMessage(message);

		MessageDto responseDto = new MessageDto(savedMessage.getId(), savedMessage.getSenderId(),
				savedMessage.getReceiverId(), savedMessage.getContent(), savedMessage.getTimestamp(),
				savedMessage.getIsRead());

		// Also send via WebSocket if connected
		messagingTemplate.convertAndSendToUser(messageDto.getReceiverId(), "/queue/messages", responseDto);

		messagingTemplate.convertAndSendToUser(messageDto.getSenderId(), "/queue/messages", responseDto);
		// Send notification
		logger.info("send Notification");
		messageService.sendNotfication(message, savedMessage.getSenderId(), savedMessage.getReceiverId());

		return ResponseEntity.ok(responseDto);
	}
}
