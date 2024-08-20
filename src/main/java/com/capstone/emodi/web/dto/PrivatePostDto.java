package com.capstone.emodi.web.dto;

import com.capstone.emodi.domain.PrivatePost;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema
public class PrivatePostDto{
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;
    private List<PrivateKeywordDto> keywordList = new ArrayList<>();

    public PrivatePostDto(PrivatePost post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.imageUrl = post.getImageUrl();
        this.createdAt = post.getCreatedAt();
        post.getKeyword().forEach(keyword -> this.keywordList.add(new PrivateKeywordDto(keyword)));
    }
}
