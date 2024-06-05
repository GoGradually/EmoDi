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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

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

    // 로그인 ID로 여러 회원 조회
    public List<Member> searchByLoginId(String loginId){
        return memberRepository.searchByLoginId(loginId).stream().filter(s->!s.getLoginId().equals(loginId)).collect(Collectors.toList());
    }

    // 회원 비밀번호 수정
    public Member updateMemberPassword(Long memberId, String password) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("해당 회원이 없습니다. id=" + memberId));
        member.changePassword(passwordEncoder.encode(password));
        return member;
    }

    // 회원 프로필 이미지 변경
    public Member updateMemberProfileImage(Long memberId, byte[] imageBytes){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("해당 회원이 없습니다. id=" + memberId));
        // 프로필 이미지가 제공된 경우
        if (imageBytes.length != 0) {
            // 새로운 프로필 이미지 저장
            String profileImageUrl = saveProfileImage(imageBytes);
            member.changeProfileImage(profileImageUrl);
        }
        return member;
    }

    // 회원 탈퇴
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("해당 회원이 없습니다. id=" + memberId));

        memberRepository.delete(member);
    }

    private String saveProfileImage(byte[] imageBytes) {
        try {
            return FileUploadUtil.saveImage(imageBytes ,uploadDir);
        } catch (IOException e) {
            // 파일 저장 실패 시 예외 처리
            throw new RuntimeException("Failed to save profile image", e);
        }
    }
}