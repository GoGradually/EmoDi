package com.capstone.emodi.web.dto;

import com.capstone.emodi.domain.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class MemberDto {
    private Long id;
    private String loginId;
    private String username;
    private String email;
    private String tellNumber;
    private String profileImage;
    private int postNum;
    private Long followingNum;
    private Long followerNum;
    private boolean isFriend;

    public MemberDto(Member member, boolean isFriend) {
        this.id = member.getId();
        this.loginId = member.getLoginId();
        this.username = member.getUsername();
        this.email = member.getEmail();
        this.tellNumber = member.getTellNumber();
        this.profileImage = member.getProfileImage();
        this.postNum = member.getPosts().size();
        this.followingNum = member.getFollowingNum();
        this.followerNum = member.getFollowerNum();
        this.isFriend = isFriend;
    }

    // Getter 메서드 생략
}