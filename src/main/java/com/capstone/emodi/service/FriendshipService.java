package com.capstone.emodi.service;

import com.capstone.emodi.domain.friendship.Friendship;
import com.capstone.emodi.domain.friendship.FriendshipRepository;
import com.capstone.emodi.domain.member.Member;
import com.capstone.emodi.domain.member.MemberRepository;
import com.capstone.emodi.web.dto.FriendshipDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final MemberRepository memberRepository;

    public FriendshipService(FriendshipRepository friendshipRepository, MemberRepository memberRepository) {
        this.friendshipRepository = friendshipRepository;
        this.memberRepository = memberRepository;
    }

    public void addFriend(Long memberId, Long friendId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));
        Member friend = memberRepository.findById(friendId).orElseThrow(() -> new IllegalArgumentException("Invalid friend ID"));
        if (friendshipRepository.existsByMemberIdAndFriendId(memberId, friendId)) {
            throw new IllegalStateException("Already friends");
        }
        Friendship friendship = new Friendship(member, friend);
        friendshipRepository.save(friendship);
    }

    public void removeFriend(Long memberId, Long friendId) {
        if (!friendshipRepository.existsByMemberIdAndFriendId(memberId, friendId)) {
            throw new IllegalStateException("Not friends yet");
        }
        friendshipRepository.deleteByMemberIdAndFriendId(memberId, friendId);
    }

    public List<Member> getFriends(Long memberId) {
        List<Friendship> frindships = friendshipRepository.findByMemberId(memberId);
        List<Member> members = new ArrayList<>();
        frindships.forEach(s -> members.add(s.getFriend()));
        return members;
    }
}

