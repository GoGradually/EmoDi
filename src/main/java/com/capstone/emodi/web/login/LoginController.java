package com.capstone.emodi.web.login;

import com.capstone.emodi.domain.login.LoginService;
import com.capstone.emodi.domain.member.Member;
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

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated LoginRequest loginRequest, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        Member loginMember = loginService.login(loginRequest.getLoginId(), loginRequest.getPassword());

        if (loginMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 또는 비밀번호가 맞지 않습니다.");
        }

        // 로그인 성공 처리
        HttpSession session = request.getSession();
        session.setAttribute("loginMember", loginMember);

        return ResponseEntity.ok(new LoginResponse(loginMember.getId(), loginMember.getLoginId(), loginMember.getUsername()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok("로그아웃되었습니다.");
    }

    @Getter
    @Setter
    private static class LoginRequest {
        @NotEmpty
        private String loginId;
        @NotEmpty
        private String password;

    }

    @Getter
    private static class LoginResponse {
        private Long id;
        private String loginId;
        private String username;

        public LoginResponse(Long id, String loginId, String username) {
            this.id = id;
            this.loginId = loginId;
            this.username = username;
        }
    }
}