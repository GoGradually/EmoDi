package com.capstone.emodi.web.member;

import com.capstone.emodi.domain.member.Member;
import com.capstone.emodi.exception.MemberNotFoundException;
import com.capstone.emodi.security.JwtTokenProvider;
import com.capstone.emodi.service.FriendshipService;
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

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final FriendshipService friendshipService;
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
        MemberDto memberResponse = new MemberDto(updatedMember,  true);
        return ResponseEntity.ok(ApiResponse.success("회원 비밀번호 수정 성공", memberResponse));

    }

    // 회원 프로필 이미지 변경
    @PutMapping("/{memberId}/profile-image")
    public ResponseEntity<ApiResponse<MemberDto>> updateMemberProfileImage(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
            @PathVariable Long memberId,
            @RequestBody byte[] imageBytes) {
        accessToken = accessToken.substring(7);
        ResponseEntity<ApiResponse<MemberDto>> UNAUTHORIZED = getMemberResponseEntity(accessToken, memberId);
        if (UNAUTHORIZED != null) return UNAUTHORIZED;
        Member updatedMember = memberService.updateMemberProfileImage(memberId, imageBytes);
        MemberDto memberResponse = new MemberDto(updatedMember, true);
        return ResponseEntity.ok(ApiResponse.success("회원 프로필 이미지 변경 성공", memberResponse));
    }

    //회원 정보 조회
    @GetMapping("/{memberId}/info")
    public ResponseEntity<ApiResponse<MemberDto>> getMemberInfo(
            @RequestHeader (HttpHeaders.AUTHORIZATION) String accessToken,
            @PathVariable Long memberId
    ){
        Member member = memberService.findById(memberId);
        accessToken = accessToken.substring(7);
        String userId = jwtTokenProvider.getLoginIdFromToken(accessToken);
        Member user = memberService.findByLoginId(userId);
        boolean isFriend = friendshipService.existFriendship(user.getId(), member.getId());

        MemberDto memberResponse = new MemberDto(member, isFriend);
        return ResponseEntity.ok(ApiResponse.success("사용자 조회 성공", memberResponse));
    }

    // 회원 조회
    @GetMapping("/member/search")
    public ResponseEntity<ApiResponse<List<MemberDto>>> getMemberList(
            @RequestHeader (HttpHeaders.AUTHORIZATION) String accessToken,
            @RequestParam String loginId){
        accessToken = accessToken.substring(7);
        String userId = jwtTokenProvider.getLoginIdFromToken(accessToken);
        Member user = memberService.findByLoginId(userId);
        List<MemberDto> membersResponse = new ArrayList<>();
        memberService.searchByLoginId(loginId).forEach(m -> membersResponse.add(new MemberDto(m, friendshipService.existFriendship(user.getId(), m.getId()))));
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("사용자 리스트 조회", membersResponse));
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
