package com.helpnearby.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.helpnearby.entities.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {

    @Query("SELECT m FROM Message m WHERE (m.senderId = :userId1 AND m.receiverId = :userId2) OR (m.senderId = :userId2 AND m.receiverId = :userId1) ORDER BY m.timestamp ASC")
    List<Message> findConversationBetweenUsers(@Param("userId1") String userId1, @Param("userId2") String userId2);

    @Query("SELECT m FROM Message m WHERE m.receiverId = :userId AND m.isRead = false")
    List<Message> findUnreadMessagesByReceiverId(@Param("userId") String userId);

    @Query("SELECT DISTINCT CASE WHEN m.senderId = :userId THEN m.receiverId ELSE m.senderId END FROM Message m WHERE m.senderId = :userId OR m.receiverId = :userId")
    List<String> findUserConversationPartners(@Param("userId") String userId);
}

