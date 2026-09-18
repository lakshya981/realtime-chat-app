package com.chatapp.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks which users are currently connected to which room, in memory.
 *
 * Uses ConcurrentHashMap because multiple WebSocket sessions (threads) can
 * join/leave concurrently - a plain HashMap would not be thread-safe here.
 *
 * In a production, multi-instance deployment this would live in Redis instead
 * of in-memory, so presence is shared across all app instances.
 */
@Service
public class PresenceService {

    private final Map<String, Set<String>> onlineUsersByRoom = new ConcurrentHashMap<>();

    public void userJoined(String roomId, String username) {
        onlineUsersByRoom
                .computeIfAbsent(roomId, key -> ConcurrentHashMap.newKeySet())
                .add(username);
    }

    public void userLeft(String roomId, String username) {
        Set<String> users = onlineUsersByRoom.get(roomId);
        if (users != null) {
            users.remove(username);
        }
    }

    public boolean isUserOnline(String roomId, String username) {
        Set<String> users = onlineUsersByRoom.get(roomId);
        return users != null && users.contains(username);
    }

    public Set<String> getOnlineUsers(String roomId) {
        return onlineUsersByRoom.getOrDefault(roomId, Set.of());
    }
}
