package com.capstone.emodi.web.dto;

import com.capstone.emodi.domain.privatepost.PrivatePost;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema
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
