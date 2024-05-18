// MemberService.java
package com.capstone.emodi.service;

import com.capstone.emodi.domain.member.Member;
import com.capstone.emodi.domain.member.MemberRepository;
import com.capstone.emodi.exception.DuplicateMemberException;
import com.capstone.emodi.exception.MemberNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

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

    // 로그인 ID로 회원 조회
    public Member findByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException("해당 로그인 ID의 회원이 없습니다."));
    }

    // 회원 정보 수정
    public Member updateMember(Long memberId, String password, String username, String email, String tellNumber) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("해당 회원이 없습니다. id=" + memberId));

        member.changePassword(password);
        // 필요한 경우 다른 필드 수정 메서드 호출

        return member;
    }

    // 회원 탈퇴
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("해당 회원이 없습니다. id=" + memberId));

        memberRepository.delete(member);
    }
}