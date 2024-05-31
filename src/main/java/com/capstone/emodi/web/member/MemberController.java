package com.capstone.emodi.web.member;

import com.capstone.emodi.domain.member.Member;
import com.capstone.emodi.exception.MemberNotFoundException;
import com.capstone.emodi.security.JwtTokenProvider;
import com.capstone.emodi.service.MemberService;
import com.capstone.emodi.web.dto.MemberDto;
import com.capstone.emodi.web.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;


    // 회원 비밀번호 수정
    @PutMapping("/{memberId}/password")
    public ResponseEntity<ApiResponse<MemberDto>> updateMemberPassword(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
            @PathVariable Long memberId,
            @RequestBody @Valid PasswordUpdateRequest passwordUpdateRequest) {
        accessToken = accessToken.substring(7);
        ResponseEntity<ApiResponse<MemberDto>> UNAUTHORIZED = getMemberResponseEntity(accessToken, memberId);
        if (UNAUTHORIZED != null) return UNAUTHORIZED;

        Member updatedMember = memberService.updateMemberPassword(memberId, passwordUpdateRequest.getPassword());
        MemberDto memberResponse = new MemberDto(updatedMember);
        return ResponseEntity.ok(ApiResponse.success("회원 비밀번호 수정 성공", memberResponse));

    }

    // 회원 프로필 이미지 변경
    @PutMapping("/{memberId}/profile-image")
    public ResponseEntity<ApiResponse<MemberDto>> updateMemberProfileImage(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
            @PathVariable Long memberId,
            @RequestParam(value = "profileImage") MultipartFile profileImage) {
        accessToken = accessToken.substring(7);
        ResponseEntity<ApiResponse<MemberDto>> UNAUTHORIZED = getMemberResponseEntity(accessToken, memberId);
        if (UNAUTHORIZED != null) return UNAUTHORIZED;
        Member updatedMember = memberService.updateMemberProfileImage(memberId, profileImage);
        MemberDto memberResponse = new MemberDto(updatedMember);
        return ResponseEntity.ok(ApiResponse.success("회원 프로필 이미지 변경 성공", memberResponse));
    }

    //회원 정보 조회
    @GetMapping("/{memberId}/info")
    public ResponseEntity<ApiResponse<MemberDto>> getMemberInfo(@PathVariable Long memberId){
        Member member = memberService.findById(memberId);
        MemberDto memberResponse = new MemberDto(member);
        return ResponseEntity.ok(ApiResponse.success("사용자 조회 성공", memberResponse));
    }
    //회원 프로필 이미지 조회

    // DTO 클래스


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
    private ResponseEntity<ApiResponse<MemberDto>> getMemberResponseEntity(String accessToken, Long memberId) {
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
    public ResponseEntity<ApiResponse<MemberDto>> handleMemberNotFoundException(MemberNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
    }
}
