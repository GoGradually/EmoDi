package com.capstone.emodi.service;

import static org.junit.jupiter.api.Assertions.*;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
    private LikeRepository likeRepository;

    @Autowired
    private FeedService feedService;

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
    public void testGetFriendFeed() {
        //given
        post = Post.builder()
                .title("title1")
                .content("content1")
                .member(friend1)
                .createdAt(LocalDateTime.now().minusDays(2)).build();
        postRepository.save(post);

        //when
        List<Post> feed = feedService.getFriendFeed(member.getId());

        //then
        assertEquals(1, feed.size());
        assertTrue(feed.contains(post));
    }
    @Test
    public void testGetOutOfDateFeed(){
        //given
        post = Post.builder()
                .title("title1")
                .content("content1")
                .member(friend1)
                .createdAt(LocalDateTime.now().minusDays(10))
                .build();
        postRepository.save(post);

        //when
        List<Post> feed = feedService.getFriendFeed(member.getId());

        //then
        assertEquals(0, feed.size());
        assertFalse(feed.contains(post));
    }


    @Test
    public void testNonFriendPostsNotIncluded() {
        //given
        post = Post.builder()
                .title("title1")
                .content("content1")
                .member(nonFriend)
                .createdAt(LocalDateTime.now().minusDays(5))
                .build();
        postRepository.save(post);

        //when
        List<Post> feed = feedService.getFriendFeed(member.getId());

        //then
        assertEquals(0, feed.size());
        assertFalse(feed.contains(post));
    }
}
