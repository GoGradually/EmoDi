package com.capstone.emodi.service;

import com.capstone.emodi.domain.member.Member;
import com.capstone.emodi.domain.member.MemberRepository;
import com.capstone.emodi.exception.LoginFailedException;
import com.capstone.emodi.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public Map<String, String> login(String loginId, String password) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new LoginFailedException("가입되지 않은 아이디이거나 잘못된 비밀번호입니다."));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new LoginFailedException("가입되지 않은 아이디이거나 잘못된 비밀번호입니다.");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(loginId);
        String refreshToken = jwtTokenProvider.generateRefreshToken(loginId);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }
}