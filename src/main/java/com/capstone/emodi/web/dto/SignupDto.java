package com.capstone.emodi.web.dto;

import lombok.Data;

@Data
public class SignupDto {
    private String message;
    private String accessToken;
    private String refreshToken;

    public SignupDto(String message, String accessToken, String refreshToken) {
        this.message = message;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
