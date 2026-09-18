package com.chatapp;

import com.chatapp.model.ChatMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ChatMessageTest {

    @Test
    void constructorSetsAllFieldsCorrectly() {
        ChatMessage message = new ChatMessage(ChatMessage.MessageType.CHAT, "general", "Aman", "Hello, world!");

        assertEquals(ChatMessage.MessageType.CHAT, message.getType());
        assertEquals("general", message.getRoomId());
        assertEquals("Aman", message.getSender());
        assertEquals("Hello, world!", message.getContent());
        assertNotNull(message.getTimestamp());
    }

    @Test
    void settersUpdateFieldsCorrectly() {
        ChatMessage message = new ChatMessage();
        message.setType(ChatMessage.MessageType.JOIN);
        message.setRoomId("tech-talk");
        message.setSender("Priya");
        message.setContent("Priya joined the chat");

        assertEquals(ChatMessage.MessageType.JOIN, message.getType());
        assertEquals("tech-talk", message.getRoomId());
        assertEquals("Priya", message.getSender());
    }
}
