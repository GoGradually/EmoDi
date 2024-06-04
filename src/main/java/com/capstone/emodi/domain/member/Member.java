package com.capstone.emodi.domain.member;

import com.capstone.emodi.domain.post.Post;
import com.capstone.emodi.domain.privatepost.PrivatePost;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        indexes = {
                @Index(name="idx_login_id", columnList = "loginId")
        },
        uniqueConstraints = {
        @UniqueConstraint(columnNames = "loginId"),
        @UniqueConstraint(columnNames = "email")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, length = 20)
    private String loginId;

    @Column(nullable = false, length = 20)
    private String username;

    @Column(nullable = false)
    private String password;


    @Email
    @Column(nullable = false)
    private String email;


    @Column(nullable = false)
    private String tellNumber;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();


    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PrivatePost> privatePosts = new ArrayList<>();

    @Column(nullable = true)
    private String profileImage = "default-image.png";

    private Long followingNum = 0L;
    private Long followerNum = 0L;
    @Builder
    public Member(String loginId, String username, String password, String email, String tellNumber, String profileImage) {
        this.loginId = loginId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.tellNumber = tellNumber;
        this.profileImage = profileImage;
    }

    // 프로필 이미지 변경 메서드 추가
    public void changeProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
    @Builder
    public Member(String loginId, String username, String password, String email, String tellNumber) {
        this.loginId = loginId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.tellNumber = tellNumber;
    }

    // 의미 있는 메서드 추가
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }
    public void addFollowing(){
        this.followingNum += 1;
    }
    public void addFollower(){
        this.followerNum += 1;
    }
    public void subFollowing(){
        this.followingNum -= 1;
    }
    public void subFollower(){
        this.followerNum -= 1;
    }
    public String getImageUrl(){
        return "https://emo-di.com/profileImages/" + profileImage;
    }
}
