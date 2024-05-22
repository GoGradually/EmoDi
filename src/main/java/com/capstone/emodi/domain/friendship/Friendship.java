package com.capstone.emodi.domain.friendship;

import com.capstone.emodi.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id")
    private Member friend;

    public Friendship(Member member, Member friend){
        this.member = member;
        this.friend = friend;
    }

    public Friendship() {

    }
    // Constructors, getters, and setters
}