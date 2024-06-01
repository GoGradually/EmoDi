package com.capstone.emodi.web.dto;

import com.capstone.emodi.domain.keyword.Keyword;
import com.capstone.emodi.domain.post.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema
public class PostDto {
    private String title;
    private String content;
    private int likeCount;
    private String imagePath;
    private String memberLoginId;
    private LocalDateTime createdAt;
    private List<Keyword> keywordList;
    public PostDto(Post post){
        this.title = post.getTitle();
        this.content = post.getContent();
        this.likeCount = post.getLikeCount();
        this.imagePath = post.getImagePath();
        this.memberLoginId = post.getMember().getLoginId();
        this.createdAt = post.getCreatedAt();
        this.keywordList = post.getKeyword();
    }
}