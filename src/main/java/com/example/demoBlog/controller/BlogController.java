package com.example.demoBlog.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demoBlog.dto.BlogRequest;
import com.example.demoBlog.model.Blog;
import com.example.demoBlog.model.User;
import com.example.demoBlog.repository.UserRepository;
import com.example.demoBlog.service.BlogService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogController {
    private final BlogService blogService;
    private final UserRepository userRepository;

    // PROTECTED ENDPOINTS - JWT AUTHENTICATION REQUIRED
    
    @PostMapping
    public ResponseEntity<Blog> createBlog(@RequestBody BlogRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Blog blog = blogService.createBlog(user, request.getTitle(), request.getContent());
        return ResponseEntity.ok(blog);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Blog> updateBlog(@PathVariable Long id, @RequestBody BlogRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Blog blog = blogService.updateBlog(user, id, request.getTitle(), request.getContent());
        return ResponseEntity.ok(blog);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBlog(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        blogService.deleteBlog(user, id);
        return ResponseEntity.ok("Blog deleted");
    }

    @GetMapping("/my-blogs")
    public ResponseEntity<List<Blog>> getMyBlogs(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        List<Blog> blogs = blogService.getBlogs(user);
        return ResponseEntity.ok(blogs);
    }

    // PUBLIC ENDPOINTS - NO AUTHENTICATION REQUIRED (READ ONLY)
    
    @GetMapping("/public")
    public ResponseEntity<List<Blog>> getAllBlogsPublic() {
        List<Blog> allBlogs = blogService.getAllBlogs();
        return ResponseEntity.ok(allBlogs);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<Blog> getBlogByIdPublic(@PathVariable Long id) {
        Blog blog = blogService.getBlogById(id);
        return ResponseEntity.ok(blog);
    }

    @GetMapping("/public/user/{username}")
    public ResponseEntity<List<Blog>> getBlogsByUsernamePublic(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        List<Blog> blogs = blogService.getBlogs(user);
        return ResponseEntity.ok(blogs);
    }
}