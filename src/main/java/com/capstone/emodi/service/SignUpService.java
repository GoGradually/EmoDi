package com.capstone.emodi.service;

import com.capstone.emodi.domain.member.Member;
import com.capstone.emodi.domain.member.MemberRepository;
import com.capstone.emodi.utils.FileUploadUtil;
import com.capstone.emodi.web.signup.SignUpController;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    public void signUp(SignUpController.SignupRequest signupRequest) {
        // 회원 정보 저장
        String profileImageUrl = "default-image.png";

        Member member = Member.builder()
                .loginId(signupRequest.getLoginId())
                .username(signupRequest.getUsername())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .email(signupRequest.getEmail())
                .tellNumber(signupRequest.getTellNumber())
                .profileImage(profileImageUrl)
                .build();

        memberService.join(member);

    }


}