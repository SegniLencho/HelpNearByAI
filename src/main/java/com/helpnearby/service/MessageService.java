package com.helpnearby.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.helpnearby.entities.Message;
import com.helpnearby.repository.MessageRepository;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Caching(evict = {
        @CacheEvict(value = "conversations", key = "#message.senderId + ':' + #message.receiverId"),
        @CacheEvict(value = "conversations", key = "#message.receiverId + ':' + #message.senderId"),
        @CacheEvict(value = "unreadMessages", key = "#message.receiverId"),
        @CacheEvict(value = "conversationPartners", key = "#message.senderId"),
        @CacheEvict(value = "conversationPartners", key = "#message.receiverId")
    })
    public Message createMessage(Message message) {
        if (message.getTimestamp() == null) {
            message.setTimestamp(LocalDateTime.now());
        }
        return messageRepository.save(message);
    }

    @Cacheable(value = "conversations", key = "#userId1 + ':' + #userId2")
    public List<Message> getConversationBetweenUsers(String userId1, String userId2) {
        return messageRepository.findConversationBetweenUsers(userId1, userId2);
    }

    @Cacheable(value = "unreadMessages", key = "#userId")
    public List<Message> getUnreadMessages(String userId) {
        return messageRepository.findUnreadMessagesByReceiverId(userId);
    }

    @Cacheable(value = "conversationPartners", key = "#userId")
    public List<String> getUserConversationPartners(String userId) {
        return messageRepository.findUserConversationPartners(userId);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "unreadMessages", key = "#receiverId"),
        @CacheEvict(value = "conversations", key = "#senderId + ':' + #receiverId"),
        @CacheEvict(value = "conversations", key = "#receiverId + ':' + #senderId")
    })
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

