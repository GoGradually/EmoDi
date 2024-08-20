package com.capstone.emodi.web.dto;

import com.capstone.emodi.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class LoginDto {
    private String message;
    private String accessToken;
    private String refreshToken;
    private MemberDto memberDto;

    public LoginDto(String message, Member member, String accessToken, String refreshToken) {
        this.message = message;
        this.memberDto = new MemberDto(member, false);
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
