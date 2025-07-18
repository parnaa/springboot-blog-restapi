package com.example.demoBlog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPresence {
    private String username;
    private boolean online;
    private String lastSeen;
    private String status; // online, offline, typing, away
}
