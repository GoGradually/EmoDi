package com.capstone.emodi.service;

import com.capstone.emodi.domain.friendship.Friendship;
import com.capstone.emodi.domain.friendship.FriendshipRepository;
import com.capstone.emodi.domain.member.Member;
import com.capstone.emodi.domain.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class FriendshipServiceTest {
    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FriendshipService friendshipService;

    private Member member;
    private Member friend;

    @BeforeEach
    public void setup() {
        member = new Member("member1", "member1","password", "member1@example.com", "010-1234-5678");
        friend = new Member("friend1","friend1", "password", "friend1@example.com", "010-8765-4321");
        memberRepository.save(member);
        memberRepository.save(friend);
    }

    @Test
    public void testAddFriend() {
        friendshipService.addFriend(member.getId(), friend.getId());

        assertTrue(friendshipRepository.existsByMemberIdAndFriendId(member.getId(), friend.getId()));
    }

    @Test
    public void testAddFriendAlreadyFriends() {
        friendshipService.addFriend(member.getId(), friend.getId());

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            friendshipService.addFriend(member.getId(), friend.getId());
        });

        assertEquals("Already friends", exception.getMessage());
    }

    @Test
    public void testRemoveFriend() {
        friendshipService.addFriend(member.getId(), friend.getId());
        friendshipService.removeFriend(member.getId(), friend.getId());

        assertFalse(friendshipRepository.existsByMemberIdAndFriendId(member.getId(), friend.getId()));
    }

    @Test
    public void testRemoveFriendNotFriends() {
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            friendshipService.removeFriend(member.getId(), friend.getId());
        });

        assertEquals("Not friends yet", exception.getMessage());
    }
}