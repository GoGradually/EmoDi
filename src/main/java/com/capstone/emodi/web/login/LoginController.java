package com.capstone.emodi.web.login;

import com.capstone.emodi.service.LoginService;
import com.capstone.emodi.web.dto.LoginDto;
import com.capstone.emodi.web.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginDto>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Map<String, String> tokens = loginService.login(loginRequest.getLoginId(), loginRequest.getPassword());
            LoginDto LoginDto = new LoginDto("로그인 성공", tokens.get("accessToken"), tokens.get("refreshToken"));
            return ResponseEntity.ok(ApiResponse.success("로그인 성공", LoginDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(e.getMessage()));
        }
    }
    @Getter
    @Setter
    static class LoginRequest {
        @NotBlank
        private String loginId;
        @NotBlank
        private String password;
    }


}