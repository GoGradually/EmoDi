package com.capstone.emodi.service;

import com.capstone.emodi.domain.member.Member;
import com.capstone.emodi.domain.member.MemberRepository;
import com.capstone.emodi.utils.FileUploadUtil;
import com.capstone.emodi.web.signup.SignUpController;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.capstone.emodi.domain.member.Member;
import com.capstone.emodi.domain.member.MemberRepository;
import com.capstone.emodi.security.JwtTokenProvider;
import com.capstone.emodi.web.signup.SignUpController;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SignUpService {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public Map<String, String> signUp(SignUpController.SignupRequest signupRequest, MultipartFile profileImage) {
        // 회원 정보 저장
        String profileImageUrl = "/images/default-profile.jpg";
        if (profileImage != null && !profileImage.isEmpty()) {
            profileImageUrl = saveProfileImage(profileImage);
        }

        Member member = Member.builder()
                .loginId(signupRequest.getLoginId())
                .username(signupRequest.getUsername())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .email(signupRequest.getEmail())
                .tellNumber(signupRequest.getTellNumber())
                .profileImage(profileImageUrl)
                .build();

        memberService.join(member);

        // 토큰 발급
        String accessToken = jwtTokenProvider.generateAccessToken(member.getLoginId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(member.getLoginId());

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }

    private String saveProfileImage(MultipartFile profileImage) {
        try {
            String uploadDir = "profile-images";
            return FileUploadUtil.saveImage(profileImage, uploadDir);
        } catch (IOException e) {
            // 파일 저장 실패 시 예외 처리
            throw new RuntimeException("Failed to save profile image", e);
        }
    }
}