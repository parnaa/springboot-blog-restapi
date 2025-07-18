package com.example.demoBlog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String content;
    private String sender;
    private MessageType type;
    private String timestamp;
    private String room; // For chat rooms/channels

    public enum MessageType {
        CHAT, JOIN, LEAVE, TYPING
    }
}
