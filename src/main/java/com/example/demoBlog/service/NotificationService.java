package com.example.demoBlog.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.demoBlog.dto.NotificationMessage;
import com.example.demoBlog.model.Blog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendBlogCreatedNotification(Blog blog, String authorUsername) {
        NotificationMessage notification = new NotificationMessage(
            "📝 New blog posted: " + blog.getTitle() + " by " + authorUsername,
            "BLOG_CREATED",
            authorUsername,
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            blog
        );
        
        log.info("Sending blog created notification: {}", blog.getTitle());
        messagingTemplate.convertAndSend("/topic/blog-notifications", notification);
    }

    public void sendBlogUpdatedNotification(Blog blog, String authorUsername) {
        NotificationMessage notification = new NotificationMessage(
            "✏️ Blog updated: " + blog.getTitle() + " by " + authorUsername,
            "BLOG_UPDATED",
            authorUsername,
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            blog
        );
        
        log.info("Sending blog updated notification: {}", blog.getTitle());
        messagingTemplate.convertAndSend("/topic/blog-notifications", notification);
    }

    public void sendBlogDeletedNotification(Blog blog, String authorUsername) {
        NotificationMessage notification = new NotificationMessage(
            "🗑️ Blog deleted: " + blog.getTitle() + " by " + authorUsername,
            "BLOG_DELETED",
            authorUsername,
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            blog
        );
        
        log.info("Sending blog deleted notification: {}", blog.getTitle());
        messagingTemplate.convertAndSend("/topic/blog-notifications", notification);
    }

    public void sendUserJoinedNotification(String username) {
        NotificationMessage notification = new NotificationMessage(
            "👋 " + username + " joined the platform!",
            "USER_JOINED",
            "System",
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            username
        );
        
        log.info("Sending user joined notification: {}", username);
        messagingTemplate.convertAndSend("/topic/user-notifications", notification);
    }

    public void sendSystemNotification(String message, String type) {
        NotificationMessage notification = new NotificationMessage(
            "🔔 " + message,
            type,
            "System",
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            null
        );
        
        log.info("Sending system notification: {}", message);
        messagingTemplate.convertAndSend("/topic/system-notifications", notification);
    }

    public void sendPrivateNotification(String username, String message, String type, Object data) {
        NotificationMessage notification = new NotificationMessage(
            message,
            type,
            "System",
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            data
        );
        
        log.info("Sending private notification to {}: {}", username, message);
        messagingTemplate.convertAndSendToUser(username, "/queue/notifications", notification);
    }
}
