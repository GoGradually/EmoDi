package com.capstone.emodi.web.dto;

import com.capstone.emodi.domain.member.Member;

public class MemberDto {
    private Long id;
    private String loginId;
    private String username;
    private String email;
    private String tellNumber;

    public MemberDto(Member member) {
        this.id = member.getId();
        this.loginId = member.getLoginId();
        this.username = member.getUsername();
        this.email = member.getEmail();
        this.tellNumber = member.getTellNumber();
    }

    // Getter 메서드 생략
}