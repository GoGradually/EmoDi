package com.capstone.emodi.web.dto;

import com.capstone.emodi.domain.post.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema
public class PostDto {
    private String title;
    private String content;
    private int likeCount;
    private String imagePath;
    private String memberLoginId;
    private LocalDateTime createdAt;
    public PostDto(Post post){
        this.title = post.getTitle();
        this.content = post.getContent();
        this.likeCount = post.getLikeCount();
        this.imagePath = post.getImagePath();
        this.memberLoginId = post.getMember().getLoginId();
        this.createdAt = post.getCreatedAt();
    }
}