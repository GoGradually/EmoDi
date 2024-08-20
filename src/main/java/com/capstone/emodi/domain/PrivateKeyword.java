package com.capstone.emodi.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "private_keywords")
@Getter
public class PrivateKeyword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "private_post_id")
    private PrivatePost privatePost;

    private String keywordTag;

    public PrivateKeyword(PrivatePost post, String keywordTag){
        this.privatePost = post;
        this.keywordTag = keywordTag;
    }

    public PrivateKeyword() {
    }

}