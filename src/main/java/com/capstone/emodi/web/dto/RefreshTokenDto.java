package com.capstone.emodi.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class RefreshTokenDto {
    private String accessToken;

    public RefreshTokenDto(String accessToken) {
        this.accessToken = accessToken;
    }
}