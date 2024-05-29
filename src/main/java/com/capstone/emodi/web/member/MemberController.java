package com.capstone.emodi.web.member;

import com.capstone.emodi.domain.member.Member;
import com.capstone.emodi.domain.post.Post;
import com.capstone.emodi.exception.MemberNotFoundException;
import com.capstone.emodi.exception.PostNotFoundException;
import com.capstone.emodi.security.JwtTokenProvider;
import com.capstone.emodi.service.MemberService;
import com.capstone.emodi.web.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;


    // 회원 비밀번호 수정
    @PutMapping("/{loginId}/password")
    public ResponseEntity<ApiResponse<MemberResponse>> updateMemberPassword(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
            @PathVariable String loginId,
            @RequestBody @Valid PasswordUpdateRequest passwordUpdateRequest) {
        Long memberId = memberService.findByLoginId(loginId).getId();
        accessToken = accessToken.substring(7);
        ResponseEntity<ApiResponse<MemberResponse>> UNAUTHORIZED = getMemberResponseEntity(accessToken, memberId);
        if (UNAUTHORIZED != null) return UNAUTHORIZED;

        Member updatedMember = memberService.updateMemberPassword(memberId, passwordUpdateRequest.getPassword());
        MemberResponse memberResponse = new MemberResponse(updatedMember);
        return ResponseEntity.ok(ApiResponse.success("회원 비밀번호 수정 성공", memberResponse));

    }

    // 회원 프로필 이미지 변경
    @PutMapping("/{loginId}/profile-image")
    public ResponseEntity<ApiResponse<MemberResponse>> updateMemberProfileImage(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
            @PathVariable String loginId,
            @RequestParam(value = "profileImage") MultipartFile profileImage) {
        Long memberId = memberService.findByLoginId(loginId).getId();
        accessToken = accessToken.substring(7);
        ResponseEntity<ApiResponse<MemberResponse>> UNAUTHORIZED = getMemberResponseEntity(accessToken, memberId);
        if (UNAUTHORIZED != null) return UNAUTHORIZED;
        Member updatedMember = memberService.updateMemberProfileImage(memberId, profileImage);
        MemberResponse memberResponse = new MemberResponse(updatedMember);
        return ResponseEntity.ok(ApiResponse.success("회원 프로필 이미지 변경 성공", memberResponse));
    }

    //회원 정보 조회
    @GetMapping("/{loginId}/info")
    public ResponseEntity<ApiResponse<MemberResponse>> getMemberInfo(@PathVariable String loginId){
        Member member = memberService.findByLoginId(loginId);
        MemberResponse memberResponse = new MemberResponse(member);
        return ResponseEntity.ok(ApiResponse.success("사용자 조회 성공", memberResponse));
    }
    //회원 프로필 이미지 조회

    // DTO 클래스
    private static class MemberResponse {
        private Long id;
        private String loginId;
        private String username;
        private String email;
        private String tellNumber;

        public MemberResponse(Member member) {
            this.id = member.getId();
            this.loginId = member.getLoginId();
            this.username = member.getUsername();
            this.email = member.getEmail();
            this.tellNumber = member.getTellNumber();
        }

        // Getter 메서드 생략
    }

    static class PasswordUpdateRequest {
        private String password;

        public PasswordUpdateRequest(String newPassword) {
            this.password = newPassword;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
    private ResponseEntity<ApiResponse<MemberResponse>> getMemberResponseEntity(String accessToken, Long memberId) {
        if (!jwtTokenProvider.validateAccessToken(accessToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("권한이 없습니다."));
        }
        // 권한 체크
        String loginId = jwtTokenProvider.getLoginIdFromToken(accessToken);
        Member member = memberService.findById(memberId);
        if (!member.getLoginId().equals(loginId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("접근 거부됨."));
        }
        return null;
    }
    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ApiResponse<MemberResponse>> handleMemberNotFoundException(MemberNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
    }
}
