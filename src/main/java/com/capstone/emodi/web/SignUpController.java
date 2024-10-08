package com.capstone.emodi.web;

import com.capstone.emodi.exception.DuplicateMemberException;
import com.capstone.emodi.service.SignUpService;
import com.capstone.emodi.dto.SignupDto;
import com.capstone.emodi.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SignUpController {
    private final SignUpService signUpService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupDto>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            signUpService.signUp(signupRequest);
            SignupDto signupDto = new SignupDto("회원가입이 완료되었습니다.");
            return ResponseEntity.ok(ApiResponse.success("회원가입 성공", signupDto));
        } catch (DuplicateMemberException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(e.getMessage()));
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


}