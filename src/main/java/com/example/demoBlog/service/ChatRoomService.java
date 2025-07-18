package com.example.demoBlog.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.stereotype.Service;

import com.example.demoBlog.dto.ChatRoom;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChatRoomService {

    // In-memory storage for chat rooms (in production, use database)
    private final Map<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> roomMembers = new ConcurrentHashMap<>();

    public ChatRoomService() {
        // Initialize default chat rooms
        createDefaultRooms();
    }

    private void createDefaultRooms() {
        ChatRoom generalRoom = new ChatRoom(
            "general",
            "General Discussion",
            "General chat for all users",
            "System",
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            false,
            0
        );
        
        ChatRoom blogRoom = new ChatRoom(
            "blog-discussions",
            "Blog Discussions",
            "Discuss blogs and share feedback",
            "System",
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            false,
            0
        );

        ChatRoom techRoom = new ChatRoom(
            "tech-talk",
            "Tech Talk",
            "Technical discussions and programming topics",
            "System",
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            false,
            0
        );

        chatRooms.put("general", generalRoom);
        chatRooms.put("blog-discussions", blogRoom);
        chatRooms.put("tech-talk", techRoom);

        roomMembers.put("general", new CopyOnWriteArraySet<>());
        roomMembers.put("blog-discussions", new CopyOnWriteArraySet<>());
        roomMembers.put("tech-talk", new CopyOnWriteArraySet<>());

        log.info("Default chat rooms created: {}", chatRooms.keySet());
    }

    public ChatRoom createRoom(String roomId, String roomName, String description, String createdBy, boolean isPrivate) {
        if (chatRooms.containsKey(roomId)) {
            throw new RuntimeException("Room with ID " + roomId + " already exists");
        }

        ChatRoom room = new ChatRoom(
            roomId,
            roomName,
            description,
            createdBy,
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            isPrivate,
            0
        );

        chatRooms.put(roomId, room);
        roomMembers.put(roomId, new CopyOnWriteArraySet<>());
        
        log.info("Created new chat room: {} by {}", roomName, createdBy);
        return room;
    }

    public void joinRoom(String roomId, String username) {
        if (!chatRooms.containsKey(roomId)) {
            throw new RuntimeException("Room not found: " + roomId);
        }

        Set<String> members = roomMembers.get(roomId);
        members.add(username);
        
        ChatRoom room = chatRooms.get(roomId);
        room.setMemberCount(members.size());
        
        log.info("User {} joined room {}", username, roomId);
    }

    public void leaveRoom(String roomId, String username) {
        if (!chatRooms.containsKey(roomId)) {
            return;
        }

        Set<String> members = roomMembers.get(roomId);
        members.remove(username);
        
        ChatRoom room = chatRooms.get(roomId);
        room.setMemberCount(members.size());
        
        log.info("User {} left room {}", username, roomId);
    }

    public ChatRoom getRoom(String roomId) {
        return chatRooms.get(roomId);
    }

    public Map<String, ChatRoom> getAllRooms() {
        return new ConcurrentHashMap<>(chatRooms);
    }

    public Set<String> getRoomMembers(String roomId) {
        return roomMembers.getOrDefault(roomId, new CopyOnWriteArraySet<>());
    }

    public boolean isUserInRoom(String roomId, String username) {
        Set<String> members = roomMembers.get(roomId);
        return members != null && members.contains(username);
    }
}
