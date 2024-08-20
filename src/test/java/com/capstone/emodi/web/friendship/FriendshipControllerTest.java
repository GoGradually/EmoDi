package com.capstone.emodi.web.friendship;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.capstone.emodi.domain.Friendship;
import com.capstone.emodi.repository.FriendshipRepository;
import com.capstone.emodi.domain.Member;
import com.capstone.emodi.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FriendshipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

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
    @WithMockUser
    public void testAddFriend() throws Exception {
        mockMvc.perform(post("/friends/" + member.getId() + "/add/" + friend.getId()))
                .andExpect(status().isOk());

        assertTrue(friendshipRepository.existsByMemberIdAndFriendId(member.getId(), friend.getId()));
    }

    @Test
    @WithMockUser
    public void testRemoveFriend() throws Exception {
        friendshipRepository.save(new Friendship(member, friend));

        mockMvc.perform(delete("/friends/" + member.getId() + "/remove/" + friend.getId()))
                .andExpect(status().isOk());

        assertFalse(friendshipRepository.existsByMemberIdAndFriendId(member.getId(), friend.getId()));
    }

    @Test
    @WithMockUser
    public void testGetFriends() throws Exception {
        friendshipRepository.save(new Friendship(member, friend));

        mockMvc.perform(get("/friends/" + member.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("친구 목록 조회 성공"))
                .andExpect(jsonPath("$.data[0].memberId").value(member.getId().intValue()))
                .andExpect(jsonPath("$.data[0].friendId").value(friend.getId().intValue()));
    }
}