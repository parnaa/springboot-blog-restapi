package com.example.demoBlog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    private String message;
    private String type; // BLOG_CREATED, BLOG_UPDATED, BLOG_DELETED, USER_JOINED, etc.
    private String sender;
    private String timestamp;
    private Object data; // Additional data (blog info, user info, etc.)
}
