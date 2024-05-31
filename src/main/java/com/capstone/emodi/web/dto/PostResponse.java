package com.capstone.emodi.web.dto;

import com.capstone.emodi.domain.post.Post;

import java.time.LocalDateTime;

public class PostResponse{
    private String title;
    private String content;
    private int likeCount;
    private String imagePath;
    private String memberLoginId;
    private LocalDateTime createdAt;
    public PostResponse(Post post){
        this.title = post.getTitle();
        this.content = post.getContent();
        this.likeCount = post.getLikeCount();
        this.imagePath = post.getImagePath();
        this.memberLoginId = post.getMember().getLoginId();
        this.createdAt = post.getCreatedAt();
    }
}