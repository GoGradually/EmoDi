package com.capstone.emodi.web.login;

import com.capstone.emodi.service.LoginService;
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
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
        try {
            Map<String, String> tokens = loginService.login(loginRequest.getLoginId(), loginRequest.getPassword());
            return ResponseEntity.ok(new LoginResponse("로그인 성공", tokens.get("accessToken"), tokens.get("refreshToken")));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse(e.getMessage(), null, null));
        }
    }

    @Getter
    @Setter
    private static class LoginRequest {
        @NotBlank
        private String loginId;
        @NotBlank
        private String password;
    }

    @Getter
    @Setter
    private static class LoginResponse {
        private String message;
        private String accessToken;
        private String refreshToken;

        public LoginResponse(String message, String accessToken, String refreshToken) {
            this.message = message;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }
}