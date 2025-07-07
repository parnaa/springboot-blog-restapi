package com.example.demoBlog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demoBlog.dto.LoginRequest;
import com.example.demoBlog.dto.RegisterRequest;
import com.example.demoBlog.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        System.out.println("Register endpoint called with username: " + request.getUsername());
        try {
            userService.register(request.getUsername(), request.getPassword());
            return ResponseEntity.ok("User registered successfully");
        } catch (RuntimeException e) {
            System.out.println("RuntimeException: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            return ResponseEntity.ok("Login successful");
        } catch (org.springframework.security.authentication.BadCredentialsException | 
                 org.springframework.security.authentication.DisabledException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Test endpoint working");
    }
}