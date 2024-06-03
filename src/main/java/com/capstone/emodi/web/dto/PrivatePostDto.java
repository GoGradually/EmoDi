package com.capstone.emodi.web.dto;

import com.capstone.emodi.domain.privatekeyword.PrivateKeyword;
import com.capstone.emodi.domain.privatepost.PrivatePost;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema
public class PrivatePostDto{
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;
    private List<PrivateKeywordDto> keywordList;

    public PrivatePostDto(PrivatePost post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.imageUrl = post.getImageUrl();
        this.createdAt = post.getCreatedAt();
        post.getKeyword().forEach(keyword -> this.keywordList.add(new PrivateKeywordDto(keyword)));
    }
}
