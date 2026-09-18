package com.chatapp.controller;

import com.chatapp.model.ChatMessage;
import com.chatapp.repository.ChatMessageRepository;
import com.chatapp.service.NotificationService;
import com.chatapp.service.PresenceService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;
    private final PresenceService presenceService;
    private final NotificationService notificationService;

    // Demo-only in-memory registry mapping username -> phone number, so the
    // offline-notification feature has something to notify. In a real app
    // this would be a proper "users" table tied to authentication.
    private final Map<String, String> userPhoneNumbers = new ConcurrentHashMap<>();

    public ChatController(ChatMessageRepository chatMessageRepository,
                           PresenceService presenceService,
                           NotificationService notificationService) {
        this.chatMessageRepository = chatMessageRepository;
        this.presenceService = presenceService;
        this.notificationService = notificationService;
    }

    /** Optional: lets a client register a phone number to receive offline SMS alerts. */
    public void registerPhoneNumber(String username, String phoneNumber) {
        userPhoneNumbers.put(username, phoneNumber);
    }

    @MessageMapping("/chat.sendMessage/{roomId}")
    @SendTo("/topic/room.{roomId}")
    public ChatMessage sendMessage(@DestinationVariable String roomId, ChatMessage chatMessage) {
        chatMessage.setRoomId(roomId);
        chatMessageRepository.save(chatMessage);

        // If the recipient(s) in this room are offline, notify via SMS.
        // (Simplified: in a 1-room demo we just check everyone who has a
        // registered phone number and isn't currently marked online.)
        userPhoneNumbers.forEach((username, phoneNumber) -> {
            boolean isSender = username.equals(chatMessage.getSender());
            if (!isSender && !presenceService.isUserOnline(roomId, username)) {
                notificationService.notifyOfflineUser(phoneNumber, chatMessage.getSender(), chatMessage.getContent());
            }
        });

        return chatMessage;
    }

    @MessageMapping("/chat.addUser/{roomId}")
    @SendTo("/topic/room.{roomId}")
    public ChatMessage addUser(@DestinationVariable String roomId,
                                ChatMessage chatMessage,
                                SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        headerAccessor.getSessionAttributes().put("roomId", roomId);
        presenceService.userJoined(roomId, chatMessage.getSender());

        chatMessage.setRoomId(roomId);
        chatMessageRepository.save(chatMessage);
        return chatMessage;
    }
}
