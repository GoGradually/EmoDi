package com.capstone.emodi.web.dto;

import com.capstone.emodi.domain.privatepost.PrivatePost;

import java.time.LocalDateTime;

public class PrivatePostDto{
    private Long id;
    private String title;
    private String content;
    private String imagePath;
    private LocalDateTime createdAt;

    public PrivatePostDto(PrivatePost post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.imagePath = post.getImagePath();
        this.createdAt = post.getCreatedAt();
    }
}
