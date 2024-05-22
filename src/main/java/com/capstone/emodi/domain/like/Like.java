package com.capstone.emodi.domain.like;

import com.capstone.emodi.domain.member.Member;
import com.capstone.emodi.domain.post.Post;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // Constructors
    public Like() {}

    public Like(Post post, Member member) {
        this.post = post;
        this.member = member;
    }

}
