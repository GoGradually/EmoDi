package com.capstone.emodi.web.auth;

import com.capstone.emodi.exception.InvalidTokenException;
import com.capstone.emodi.security.JwtTokenProvider;
import com.capstone.emodi.web.response.ApiResponse;
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
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        try {
            if (jwtTokenProvider.validateRefreshToken(refreshToken) && jwtTokenProvider.isRefreshTokenValid(refreshToken)) {
                String loginId = jwtTokenProvider.getLoginIdFromToken(refreshToken);
                String newAccessToken = jwtTokenProvider.generateAccessToken(loginId);
                return ResponseEntity.ok(new RefreshTokenResponse(newAccessToken));
            } else {
                throw new InvalidTokenException("Invalid or expired refresh token");
            }
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(e.getMessage()));
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest logoutRequest) {
        String refreshToken = logoutRequest.getRefreshToken();
        if (jwtTokenProvider.validateRefreshToken(refreshToken)) {
            jwtTokenProvider.logoutRefreshToken(refreshToken);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("Invalid refresh token");
        }
    }

    @Getter
    @Setter
    static class LogoutRequest {
        private String refreshToken;
    }

    @Getter
    @Setter
    private static class RefreshTokenRequest {
        private String refreshToken;
    }

    @Getter
    @Setter
    private static class RefreshTokenResponse {
        private String accessToken;

        public RefreshTokenResponse(String accessToken) {
            this.accessToken = accessToken;
        }
    }
}