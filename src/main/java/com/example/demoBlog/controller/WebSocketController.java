package com.example.demoBlog.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.demoBlog.dto.ChatMessage;
import com.example.demoBlog.dto.ChatRoom;
import com.example.demoBlog.dto.NotificationMessage;
import com.example.demoBlog.dto.UserPresence;
import com.example.demoBlog.service.ChatRoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomService chatRoomService;

    // Enhanced chat functionality with rooms
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return chatMessage;
    }

    @MessageMapping("/room/{roomId}/sendMessage")
    public void sendMessageToRoom(@DestinationVariable String roomId, @Payload ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        chatMessage.setRoom(roomId);
        
        // Verify user is in the room
        if (chatRoomService.isUserInRoom(roomId, chatMessage.getSender())) {
            messagingTemplate.convertAndSend("/topic/room/" + roomId, chatMessage);
        } else {
            log.warn("User {} attempted to send message to room {} without being a member", 
                    chatMessage.getSender(), roomId);
        }
    }

    @MessageMapping("/room/{roomId}/join")
    public void joinRoom(@DestinationVariable String roomId, @Payload ChatMessage chatMessage, 
                        SimpMessageHeaderAccessor headerAccessor) {
        try {
            chatRoomService.joinRoom(roomId, chatMessage.getSender());
            
            // Add room info to session
            headerAccessor.getSessionAttributes().put("currentRoom", roomId);
            headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
            
            // Broadcast join message to room
            ChatMessage joinMessage = new ChatMessage();
            joinMessage.setType(ChatMessage.MessageType.JOIN);
            joinMessage.setSender(chatMessage.getSender());
            joinMessage.setContent(chatMessage.getSender() + " joined " + roomId);
            joinMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            joinMessage.setRoom(roomId);
            
            messagingTemplate.convertAndSend("/topic/room/" + roomId, joinMessage);
            
            // Update room info for all users
            ChatRoom room = chatRoomService.getRoom(roomId);
            messagingTemplate.convertAndSend("/topic/room-updates", room);
            
        } catch (RuntimeException e) {
            log.error("Error joining room {}: {}", roomId, e.getMessage());
        }
    }

    @MessageMapping("/room/{roomId}/leave")
    public void leaveRoom(@DestinationVariable String roomId, @Payload ChatMessage chatMessage) {
        chatRoomService.leaveRoom(roomId, chatMessage.getSender());
        
        // Broadcast leave message to room
        ChatMessage leaveMessage = new ChatMessage();
        leaveMessage.setType(ChatMessage.MessageType.LEAVE);
        leaveMessage.setSender(chatMessage.getSender());
        leaveMessage.setContent(chatMessage.getSender() + " left " + roomId);
        leaveMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        leaveMessage.setRoom(roomId);
        
        messagingTemplate.convertAndSend("/topic/room/" + roomId, leaveMessage);
        
        // Update room info for all users
        ChatRoom room = chatRoomService.getRoom(roomId);
        messagingTemplate.convertAndSend("/topic/room-updates", room);
    }

    @MessageMapping("/room/create")
    public void createRoom(@Payload ChatRoom roomRequest) {
        try {
            ChatRoom room = chatRoomService.createRoom(
                roomRequest.getRoomId(),
                roomRequest.getRoomName(),
                roomRequest.getDescription(),
                roomRequest.getCreatedBy(),
                roomRequest.isPrivate()
            );
            
            // Broadcast new room to all users
            messagingTemplate.convertAndSend("/topic/new-rooms", room);
            
        } catch (RuntimeException e) {
            log.error("Error creating room: {}", e.getMessage());
        }
    }

    @MessageMapping("/room/list")
    public void listRooms() {
        Map<String, ChatRoom> rooms = chatRoomService.getAllRooms();
        messagingTemplate.convertAndSend("/topic/room-list", rooms);
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        chatMessage.setContent(chatMessage.getSender() + " joined the chat!");
        chatMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        // Broadcast user presence
        UserPresence userPresence = new UserPresence(chatMessage.getSender(), true, 
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), "online");
        messagingTemplate.convertAndSend("/topic/user-presence", userPresence);
        
        return chatMessage;
    }

    @MessageMapping("/chat.typing")
    @SendTo("/topic/typing")
    public ChatMessage userTyping(@Payload ChatMessage chatMessage) {
        chatMessage.setType(ChatMessage.MessageType.TYPING);
        chatMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return chatMessage;
    }

    @MessageMapping("/chat.private")
    public void sendPrivateMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRoom(), // recipient username
                "/queue/private",
                chatMessage
        );
    }

    // WebSocket event handlers
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String currentRoom = (String) headerAccessor.getSessionAttributes().get("currentRoom");
        
        if (username != null) {
            log.info("User Disconnected: " + username);
            
            // Leave current room if in one
            if (currentRoom != null) {
                chatRoomService.leaveRoom(currentRoom, username);
                
                ChatMessage leaveMessage = new ChatMessage();
                leaveMessage.setType(ChatMessage.MessageType.LEAVE);
                leaveMessage.setSender(username);
                leaveMessage.setContent(username + " disconnected from " + currentRoom);
                leaveMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                leaveMessage.setRoom(currentRoom);
                
                messagingTemplate.convertAndSend("/topic/room/" + currentRoom, leaveMessage);
                
                // Update room info
                ChatRoom room = chatRoomService.getRoom(currentRoom);
                if (room != null) {
                    messagingTemplate.convertAndSend("/topic/room-updates", room);
                }
            }
            
            // Broadcast user leaving global chat
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(username);
            chatMessage.setContent(username + " left the chat!");
            chatMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            messagingTemplate.convertAndSend("/topic/public", chatMessage);
            
            // Broadcast user presence offline
            UserPresence userPresence = new UserPresence(username, false, 
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), "offline");
            messagingTemplate.convertAndSend("/topic/user-presence", userPresence);
        }
    }

    // Admin notification method for system-wide announcements
    @MessageMapping("/admin.notification")
    public void sendSystemNotification(@Payload NotificationMessage notification) {
        notification.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        messagingTemplate.convertAndSend("/topic/system-notifications", notification);
    }
}
