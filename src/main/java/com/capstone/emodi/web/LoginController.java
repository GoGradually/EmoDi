package com.capstone.emodi.web;

import com.capstone.emodi.service.LoginService;
import com.capstone.emodi.service.MemberService;
import com.capstone.emodi.dto.LoginDto;
import com.capstone.emodi.response.ApiResponse;
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
    private final MemberService memberService;
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginDto>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Map<String, String> tokens = loginService.login(loginRequest.getLoginId(), loginRequest.getPassword());
            LoginDto LoginDto = new LoginDto("로그인 성공", memberService.findByLoginId(loginRequest.getLoginId()),tokens.get("accessToken"), tokens.get("refreshToken"));
            return ResponseEntity.ok(ApiResponse.success("로그인 성공", LoginDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(e.getMessage()));
        }
    }
    @Getter
    @Setter
    public static class LoginRequest {
        @NotBlank
        private String loginId;
        @NotBlank
        private String password;
    }


}