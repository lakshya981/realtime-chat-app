package com.chatapp.repository;

import com.chatapp.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Spring Data JPA auto-generates the query from this method name:
    // "find all messages for this roomId, ordered by timestamp ascending"
    List<ChatMessage> findByRoomIdOrderByTimestampAsc(String roomId);
}
