// MemberService.java
package com.capstone.emodi.service;

import com.capstone.emodi.domain.member.Member;
import com.capstone.emodi.domain.member.MemberRepository;
import com.capstone.emodi.exception.DuplicateMemberException;
import com.capstone.emodi.exception.MemberNotFoundException;
import com.capstone.emodi.utils.FileUploadUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Value("${profileImage.dir}")
    String uploadDir;

    // 회원 가입
    public Member join(Member member) {
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    // 중복 회원 검증
    private void validateDuplicateMember(Member member) {
        if (memberRepository.existsByLoginId(member.getLoginId())) {
            throw new DuplicateMemberException("이미 존재하는 로그인 ID입니다.");
        }
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new DuplicateMemberException("이미 존재하는 이메일입니다.");
        }
    }

    // DB ID로 회원 조회
    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("해당 DB ID의 회원이 없습니다."));
    }


    // 로그인 ID로 회원 조회
    public Member findByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException("해당 로그인 ID의 회원이 없습니다."));
    }

    // 회원 비밀번호 수정
    public Member updateMemberPassword(Long memberId, String password) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("해당 회원이 없습니다. id=" + memberId));

        member.changePassword(password);

        return member;
    }

    // 회원 프로필 이미지 변경
    public Member updateMemberProfileImage(Long memberId, MultipartFile image){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("해당 회원이 없습니다. id=" + memberId));
        // 프로필 이미지가 제공된 경우
        if (image != null && !image.isEmpty() && !image.getName().equals("defalut-profile.jpg")) {
            // 새로운 프로필 이미지 저장
            String profileImageUrl = saveProfileImage(image);
            member.changeProfileImage(profileImageUrl);
        } else {
            // 프로필 이미지가 제공되지 않은 경우, 기존 이미지 유지
            if (member.getProfileImage() == null || member.getProfileImage().isEmpty()) {
                // 기존 이미지가 없는 경우, 기본 이미지 설정
                member.changeProfileImage("/images/default-profile.jpg");
            }
        }

        return member;
    }

    // 회원 탈퇴
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("해당 회원이 없습니다. id=" + memberId));

        memberRepository.delete(member);
    }

    private String saveProfileImage(MultipartFile profileImage) {
        try {
            return FileUploadUtil.saveImage(profileImage, uploadDir);
        } catch (IOException e) {
            // 파일 저장 실패 시 예외 처리
            throw new RuntimeException("Failed to save profile image", e);
        }
    }
}