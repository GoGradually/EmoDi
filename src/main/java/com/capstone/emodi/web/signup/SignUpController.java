package com.capstone.emodi.web.signup;

import com.capstone.emodi.domain.signup.SignUpService;
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

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SignUpController {
    private final SignUpService signUpService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody @Valid SignupRequest signupRequest) {
        try {
            signUpService.signUp(signupRequest);
            String token = jwtTokenProvider.generateToken(signupRequest.getLoginId());
            return ResponseEntity.ok(new SignupResponse("회원가입이 완료되었습니다.", token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new SignupResponse(e.getMessage(), null));
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
        private String token;

        public SignupResponse(String message, String token) {
            this.message = message;
            this.token = token;
        }
    }
}