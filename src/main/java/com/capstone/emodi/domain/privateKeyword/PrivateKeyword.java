package com.capstone.emodi.domain.privateKeyword;

import com.capstone.emodi.domain.post.Post;
import com.capstone.emodi.domain.privatepost.PrivatePost;
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
    private PrivatePost post;

    private String keywordTag;

    public PrivateKeyword(PrivatePost post, String keywordTag){
        this.post = post;
        this.keywordTag = keywordTag;
    }

    public PrivateKeyword() {
    }

}