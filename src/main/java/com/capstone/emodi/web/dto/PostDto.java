package com.capstone.emodi.web.dto;

import com.capstone.emodi.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema
public class PostDto {
    private String title;
    private String content;
    private int likeCount;
    private String imageUrl;
    private String memberLoginId;
    private LocalDateTime createdAt;
    private List<KeywordDto> keywordList = new ArrayList<>();
    public PostDto(Post post){
        this.title = post.getTitle();
        this.content = post.getContent();
        this.likeCount = post.getLikeCount();
        this.imageUrl = post.getImageUrl();
        this.memberLoginId = post.getMember().getLoginId();
        this.createdAt = post.getCreatedAt();
        post.getKeyword().forEach(keyword -> this.keywordList.add(new KeywordDto(keyword)));
    }
}