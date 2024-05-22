package com.capstone.emodi.web.feed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import com.capstone.emodi.domain.friendship.Friendship;
import com.capstone.emodi.domain.friendship.FriendshipRepository;
import com.capstone.emodi.domain.like.Like;
import com.capstone.emodi.domain.like.LikeRepository;
import com.capstone.emodi.domain.member.Member;
import com.capstone.emodi.domain.member.MemberRepository;
import com.capstone.emodi.domain.post.Post;
import com.capstone.emodi.domain.post.PostRepository;
import com.capstone.emodi.service.FeedService;
import com.capstone.emodi.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
@Transactional
@Rollback
public class FeedIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private FeedService feedService;

    @Autowired
    private LikeService likeService;

    private Member member;
    private Member friend1;
    private Member friend2;
    private Member nonFriend;
    private Post post;

    @BeforeEach
    public void setup() {
        member = new Member("member1","member1", "password", "member1@example.com", "010-1234-5678");
        friend1 = new Member("friend1","friend1", "password", "friend1@example.com", "010-1234-9876");
        friend2 = new Member("friend2","friend2", "password", "friend2@example.com", "010-1234-9875");
        nonFriend = new Member("nonFriend","nonFriend", "password", "nonFriend@example.com", "010-8765-4321");

        memberRepository.save(member);
        memberRepository.save(friend1);
        memberRepository.save(friend2);
        memberRepository.save(nonFriend);

        Friendship friendship1 = new Friendship(member, friend1);
        Friendship friendship2 = new Friendship(member, friend2);
        Friendship friendship3 = new Friendship(friend2, friend1);
        friendshipRepository.save(friendship1);
        friendshipRepository.save(friendship2);
        friendshipRepository.save(friendship3);
    }

    @Test
    @WithMockUser
    public void testGetFriendFeed() throws Exception {
        post = Post.builder()
                .title("title1")
                .content("content1")
                .member(friend1)
                .createdAt(LocalDateTime.now().minusDays(5)).build();
        postRepository.save(post);

        mockMvc.perform(get("/feed").param("memberId", String.valueOf(member.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(post.getId()))
                .andExpect(jsonPath("$[1]").doesNotExist()); // Only 1 posts should be included
    }
    @Test
    public void testGetFriendsLikeFeed(){
        //given
        post = Post.builder()
                .title("title1")
                .content("content1")
                .member(nonFriend)
                .createdAt(LocalDateTime.now().minusDays(5)).build();
        postRepository.save(post);

        likeService.likePost(post.getId(), friend1.getId());

        //when
        List<Post> feed = feedService.getFriendFeed(member.getId());

        //then
        assertEquals(1, feed.size());
        assertTrue(feed.contains(post));
    }
    @Test
    public void testGetFriendsLikeFriendFeed(){
        //given
        post = Post.builder()
                .title("title1")
                .content("content1")
                .member(friend2)
                .createdAt(LocalDateTime.now().minusDays(5)).build();
        postRepository.save(post);

        likeService.likePost(post.getId(), friend1.getId());

        //when
        List<Post> feed = feedService.getFriendFeed(member.getId());

        //then
        assertEquals(1, feed.size());
        assertTrue(feed.contains(post));
    }

}
