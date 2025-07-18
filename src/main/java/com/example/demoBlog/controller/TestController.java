package com.example.demoBlog.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "Application is running successfully!";
    }

    @GetMapping("/test-websocket")
    public String testWebSocket() {
        return "WebSocket configuration is active. Demo page should be accessible at /websocket-demo.html";
    }
}
