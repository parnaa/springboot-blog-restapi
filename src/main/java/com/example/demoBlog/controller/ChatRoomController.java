package com.example.demoBlog.controller;

import java.util.Map;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demoBlog.dto.ChatRoom;
import com.example.demoBlog.service.ChatRoomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/rooms")
    public ResponseEntity<Map<String, ChatRoom>> getAllRooms() {
        return ResponseEntity.ok(chatRoomService.getAllRooms());
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<ChatRoom> getRoom(@PathVariable String roomId) {
        ChatRoom room = chatRoomService.getRoom(roomId);
        if (room == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(room);
    }

    @GetMapping("/rooms/{roomId}/members")
    public ResponseEntity<Set<String>> getRoomMembers(@PathVariable String roomId) {
        ChatRoom room = chatRoomService.getRoom(roomId);
        if (room == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(chatRoomService.getRoomMembers(roomId));
    }

    @PostMapping("/rooms")
    public ResponseEntity<ChatRoom> createRoom(@RequestBody ChatRoom roomRequest, 
                                             @AuthenticationPrincipal UserDetails userDetails) {
        try {
            ChatRoom room = chatRoomService.createRoom(
                roomRequest.getRoomId(),
                roomRequest.getRoomName(),
                roomRequest.getDescription(),
                userDetails.getUsername(),
                roomRequest.isPrivate()
            );
            return ResponseEntity.ok(room);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/rooms/{roomId}/join")
    public ResponseEntity<String> joinRoom(@PathVariable String roomId, 
                                         @AuthenticationPrincipal UserDetails userDetails) {
        try {
            chatRoomService.joinRoom(roomId, userDetails.getUsername());
            return ResponseEntity.ok("Joined room successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/rooms/{roomId}/leave")
    public ResponseEntity<String> leaveRoom(@PathVariable String roomId, 
                                          @AuthenticationPrincipal UserDetails userDetails) {
        chatRoomService.leaveRoom(roomId, userDetails.getUsername());
        return ResponseEntity.ok("Left room successfully");
    }
}
