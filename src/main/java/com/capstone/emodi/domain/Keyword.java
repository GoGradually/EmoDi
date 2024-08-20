package com.capstone.emodi.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "keywords")
@Getter
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private String keywordTag;

    public Keyword(Post post, String keywordTag){
        this.post = post;
        this.keywordTag = keywordTag;
    }

    public Keyword() {

    }
}
