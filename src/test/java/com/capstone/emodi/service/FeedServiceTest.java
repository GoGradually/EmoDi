package com.capstone.emodi.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import com.capstone.emodi.domain.Friendship;
import com.capstone.emodi.repository.FriendshipRepository;
import com.capstone.emodi.domain.Member;
import com.capstone.emodi.repository.MemberRepository;
import com.capstone.emodi.domain.Post;
import com.capstone.emodi.repository.PostRepository;
import com.capstone.emodi.web.dto.PostDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Rollback
public class FeedServiceTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FeedService feedService;

    private Member member;
    private Member friend1;
    private Member friend2;
    private Member nonFriend;

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
    public void testGetFriendFeed() {
        //given
        Post post1 = Post.builder()
                .title("title1")
                .content("content1")
                .member(friend1)
                .createdAt(LocalDateTime.now().minusDays(2)).build();
        Post post2 = Post.builder()
                .title("title2")
                .content("content2")
                .member(nonFriend)
                .createdAt(LocalDateTime.now().minusDays(1)).build();
        postRepository.save(post1);
        postRepository.save(post2);
        likeService.likePost(post2.getId(), friend2.getId());

        //when
        Page<PostDto> feed = feedService.getFriendFeed(member.getId(), PageRequest.of(0, 10));

        //then
        assertEquals(2, feed.getContent().size());
        assertTrue(feed.getContent().contains(post1));
        assertTrue(feed.getContent().contains(post2));
    }

    @Test
    public void testGetFriendFeedPaging() {
        //given
        for (int i = 1; i <= 15; i++) {
            Post post = Post.builder()
                    .title("title" + i)
                    .content("content" + i)
                    .member(friend1)
                    .createdAt(LocalDateTime.now().minusDays(i)).build();
            postRepository.save(post);
        }

        //when
        Page<PostDto> feed1 = feedService.getFriendFeed(member.getId(), PageRequest.of(0, 10));
        Page<PostDto> feed2 = feedService.getFriendFeed(member.getId(), PageRequest.of(1, 10));

        //then
        assertEquals(10, feed1.getContent().size());
        assertEquals(5, feed2.getContent().size());
    }


    @Test
    public void testNonFriendPostsNotIncluded() {
        //given
        Post post = Post.builder()
                .title("title1")
                .content("content1")
                .member(nonFriend)
                .createdAt(LocalDateTime.now().minusDays(5))
                .build();
        postRepository.save(post);

        //when
        Page<PostDto> feed = feedService.getFriendFeed(member.getId(), PageRequest.of(0, 10));

        //then
        assertEquals(0, feed.getContent().size());
        assertFalse(feed.getContent().contains(post));
    }
}
