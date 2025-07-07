package com.example.demoBlog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demoBlog.model.Blog;
import com.example.demoBlog.model.User;

public interface BlogRepository extends JpaRepository<Blog, Long> {
    List<Blog> findByUser(User user);
    

}