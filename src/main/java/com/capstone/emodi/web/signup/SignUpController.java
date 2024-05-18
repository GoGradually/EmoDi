package com.capstone.emodi.web.signup;

import com.capstone.emodi.service.SignUpService;
import com.capstone.emodi.security.JwtTokenProvider;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SignUpController {
    private final SignUpService signUpService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody @Valid SignupRequest signupRequest) {
        try {
            Map<String, String> tokens = signUpService.signUp(signupRequest);
            return ResponseEntity.ok(new SignupResponse("회원가입이 완료되었습니다.", tokens.get("accessToken"), tokens.get("refreshToken")));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new SignupResponse(e.getMessage(), null, null));
        }
    }

    @Getter
    @Setter
    public static class SignupRequest {
        @NotBlank
        @Size(min = 4, max = 20)
        private String loginId;

        @NotBlank
        private String username;

        @NotBlank
        @Size(min = 8, max = 20)
        private String password;

        @NotBlank
        @Email
        private String email;

        @NotBlank
        private String tellNumber;
    }

    @Getter
    @Setter
    private static class SignupResponse {
        private String message;
        private String accessToken;
        private String refreshToken;

        public SignupResponse(String message, String accessToken, String refreshToken) {
            this.message = message;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }
}