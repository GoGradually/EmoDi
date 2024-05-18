package com.capstone.emodi.web.login;

import com.capstone.emodi.domain.login.LoginService;
import com.capstone.emodi.domain.member.Member;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
        try {
            String token = loginService.login(loginRequest.getLoginId(), loginRequest.getPassword());
            return ResponseEntity.ok(new LoginResponse("로그인 성공", token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse(e.getMessage(), null));
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
        private String token;

        public LoginResponse(String message, String token) {
            this.message = message;
            this.token = token;
        }
    }
}