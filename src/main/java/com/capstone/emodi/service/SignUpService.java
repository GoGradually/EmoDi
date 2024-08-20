package com.capstone.emodi.service;

import com.capstone.emodi.domain.Member;
import com.capstone.emodi.web.SignUpController;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.capstone.emodi.security.JwtTokenProvider;

@Service
@RequiredArgsConstructor
public class SignUpService {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final FriendshipService friendshipService;
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
        friendshipService.selfFollow(member.getId());
    }
}