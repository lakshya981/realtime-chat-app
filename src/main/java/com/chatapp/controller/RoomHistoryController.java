package com.chatapp.controller;

import com.chatapp.model.ChatMessage;
import com.chatapp.repository.ChatMessageRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomHistoryController {

    private final ChatMessageRepository chatMessageRepository;

    public RoomHistoryController(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    /** Returns the persisted message history for a room, oldest first. */
    @GetMapping("/{roomId}/messages")
    public List<ChatMessage> getRoomHistory(@PathVariable String roomId) {
        return chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId);
    }
}
