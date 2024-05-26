package com.capstone.emodi.web.member;

import com.capstone.emodi.domain.member.Member;
import com.capstone.emodi.service.MemberService;
import com.capstone.emodi.web.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;


    // 회원 비밀번호 수정
    @PutMapping("/{memberId}/password")
    public ResponseEntity<ApiResponse<MemberResponse>> updateMemberPassword(
            @PathVariable Long memberId,
            @RequestBody @Valid PasswordUpdateRequest passwordUpdateRequest) {
        Member updatedMember = memberService.updateMemberPassword(memberId, passwordUpdateRequest.getPassword());
        MemberResponse memberResponse = new MemberResponse(updatedMember);
        return ResponseEntity.ok(ApiResponse.success("회원 비밀번호 수정 성공", memberResponse));
    }

    // 회원 프로필 이미지 변경
    @PutMapping("/{memberId}/profile-image")
    public ResponseEntity<ApiResponse<MemberResponse>> updateMemberProfileImage(
            @PathVariable Long memberId,
            @RequestParam(value = "profileImage") MultipartFile profileImage) {
        Member updatedMember = memberService.updateMemberProfileImage(memberId, profileImage);
        MemberResponse memberResponse = new MemberResponse(updatedMember);
        return ResponseEntity.ok(ApiResponse.success("회원 프로필 이미지 변경 성공", memberResponse));
    }
    // DTO 클래스
    private static class MemberResponse {
        private Long id;
        private String loginId;
        private String username;
        private String email;
        private String tellNumber;
        private String profileImage;

        public MemberResponse(Member member) {
            this.id = member.getId();
            this.loginId = member.getLoginId();
            this.username = member.getUsername();
            this.email = member.getEmail();
            this.tellNumber = member.getTellNumber();
            this.profileImage = member.getProfileImage();
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
}
