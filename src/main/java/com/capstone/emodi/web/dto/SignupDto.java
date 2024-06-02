package com.capstone.emodi.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class SignupDto {
    private String message;

    public SignupDto(String message) {
        this.message = message;
    }
}
