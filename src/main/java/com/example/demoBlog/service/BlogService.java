package com.example.demoBlog.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demoBlog.model.Blog;
import com.example.demoBlog.model.User;
import com.example.demoBlog.repository.BlogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlogService {
    private final BlogRepository blogRepository;

    public Blog createBlog(User user, String title, String content) {
        Blog blog = new Blog();
        blog.setTitle(title);
        blog.setContent(content);
        blog.setUser(user);
        return blogRepository.save(blog);
    }

    public List<Blog> getBlogs(User user) {
        return blogRepository.findByUser(user);
    }

    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }

    public Blog updateBlog(User user, Long blogId, String title, String content) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));
        if (!blog.getUser().getId().equals(user.getId()))
            throw new RuntimeException("Unauthorized");
        blog.setTitle(title);
        blog.setContent(content);
        return blogRepository.save(blog);
    }

    public void deleteBlog(User user, Long blogId) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));
        if (!blog.getUser().getId().equals(user.getId()))
            throw new RuntimeException("Unauthorized");
        blogRepository.delete(blog);
    }

    public Blog getBlogById(Long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found"));
    }
}