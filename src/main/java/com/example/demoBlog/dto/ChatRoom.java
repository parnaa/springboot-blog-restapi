package com.example.demoBlog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
    private String roomId;
    private String roomName;
    private String description;
    private String createdBy;
    private String createdAt;
    private boolean isPrivate;
    private int memberCount;
}
