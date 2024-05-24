package com.capstone.emodi.service;

import com.capstone.emodi.domain.member.Member;
import com.capstone.emodi.domain.member.MemberRepository;
import com.capstone.emodi.exception.DuplicateMemberException;
import com.capstone.emodi.exception.MemberNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .loginId("testuser")
                .username("테스트유저")
                .password("password")
                .email("test@example.com")
                .tellNumber("01012345678")
                .build();
    }

    @Test
    void join_success() {
        // given
        given(memberRepository.existsByLoginId(member.getLoginId())).willReturn(false);
        given(memberRepository.existsByEmail(member.getEmail())).willReturn(false);
        given(memberRepository.save(any(Member.class))).willReturn(member);

        // when
        Member savedMember = memberService.join(member);

        // then
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getLoginId()).isEqualTo(member.getLoginId());
        verify(memberRepository).save(member);
    }

    @Test
    void join_failWithDuplicateLoginId() {
        // given
        given(memberRepository.existsByLoginId(member.getLoginId())).willReturn(true);

        // when, then
        assertThatThrownBy(() -> memberService.join(member))
                .isInstanceOf(DuplicateMemberException.class)
                .hasMessageContaining("이미 존재하는 로그인 ID");
    }

    @Test
    void join_failWithDuplicateEmail() {
        // given
        given(memberRepository.existsByLoginId(member.getLoginId())).willReturn(false);
        given(memberRepository.existsByEmail(member.getEmail())).willReturn(true);

        // when, then
        assertThatThrownBy(() -> memberService.join(member))
                .isInstanceOf(DuplicateMemberException.class)
                .hasMessageContaining("이미 존재하는 이메일");
    }

    @Test
    void findByLoginId_success() {
        // given
        given(memberRepository.findByLoginId(member.getLoginId())).willReturn(Optional.of(member));

        // when
        Member foundMember = memberService.findByLoginId(member.getLoginId());

        // then
        assertThat(foundMember).isEqualTo(member);
    }

    @Test
    void findByLoginId_failWithNotFoundMember() {
        // given
        given(memberRepository.findByLoginId(member.getLoginId())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> memberService.findByLoginId(member.getLoginId()))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("해당 로그인 ID의 회원이 없습니다");
    }
    @Test
    void updateMemberPassword_success() {
        // given
        String newPassword = "newPassword";

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        // when
        Member updatedMember = memberService.updateMemberPassword(member.getId(), newPassword);

        // then
        assertThat(updatedMember.getPassword()).isEqualTo(newPassword);
    }

    @Test
    void updateMemberPassword_failWithNotFoundMember() {
        // given
        given(memberRepository.findById(member.getId())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> memberService.updateMemberPassword(member.getId(), "newPassword"))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("해당 회원이 없습니다");
    }

    @Test
    void deleteMember_success() {
        // given
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        // when
        memberService.deleteMember(member.getId());

        // then
        verify(memberRepository).delete(member);
    }

    @Test
    void deleteMember_failWithNotFoundMember() {
        // given
        given(memberRepository.findById(member.getId())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> memberService.deleteMember(member.getId()))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("해당 회원이 없습니다");
    }
}