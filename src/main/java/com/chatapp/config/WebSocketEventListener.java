package com.chatapp.config;

import com.chatapp.model.ChatMessage;
import com.chatapp.service.PresenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final PresenceService presenceService;

    public WebSocketEventListener(SimpMessagingTemplate messagingTemplate, PresenceService presenceService) {
        this.messagingTemplate = messagingTemplate;
        this.presenceService = presenceService;
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");

        if (username != null && roomId != null) {
            logger.info("User {} disconnected from room {}", username, roomId);
            presenceService.userLeft(roomId, username);

            ChatMessage leaveMessage = new ChatMessage(ChatMessage.MessageType.LEAVE, roomId, username, username + " left the chat");
            messagingTemplate.convertAndSend("/topic/room." + roomId, leaveMessage);
        }
    }
}
