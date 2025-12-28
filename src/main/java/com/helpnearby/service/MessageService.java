package com.helpnearby.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.helpnearby.entities.Message;
import com.helpnearby.repository.MessageRepository;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

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
        unreadMessages.stream()
            .filter(msg -> msg.getSenderId().equals(senderId))
            .forEach(msg -> {
                msg.setIsRead(true);
                messageRepository.save(msg);
            });
    }

    public Optional<Message> getMessageById(String id) {
        return messageRepository.findById(id);
    }
}

