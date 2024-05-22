package com.capstone.emodi.domain.post;

import com.capstone.emodi.domain.like.Like;
import com.capstone.emodi.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = true)
    private String imagePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    public int getLikeCount() {
        return likes.size();
    }

    @PrePersist
    public void setCreatedAt() {
        this.createdAt = this.createdAt == null ? LocalDateTime.now() : this.createdAt;
    }
    @Builder
    public Post(String title, String content, String imagePath, LocalDateTime createdAt, Member member) {
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
        this.createdAt = createdAt;
        this.member = member;
    }
    @Builder
    public Post(String title, String content, LocalDateTime createdAt, Member member) {
        this.title = title;
        this.content = content;
        this.member = member;
        this.createdAt = createdAt;
    }
    @Builder
    public Post(String title, String content, String imagePath, Member member) {
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
        this.member = member;
    }
    @Builder
    public Post(String title, String content, Member member) {
        this.title = title;
        this.content = content;
        this.member = member;
    }



    // 게시글 수정 메서드
    public void update(String title, String content, String imagePath) {
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
    }

}
