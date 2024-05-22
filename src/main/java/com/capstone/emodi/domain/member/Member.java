package com.capstone.emodi.domain.member;

import com.capstone.emodi.domain.post.Post;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = {
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
}
