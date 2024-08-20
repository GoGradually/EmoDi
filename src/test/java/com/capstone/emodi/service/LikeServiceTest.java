package com.capstone.emodi.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import java.util.Optional;

import com.capstone.emodi.domain.Like;
import com.capstone.emodi.repository.LikeRepository;
import com.capstone.emodi.domain.Member;
import com.capstone.emodi.repository.MemberRepository;
import com.capstone.emodi.domain.Post;
import com.capstone.emodi.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class LikeServiceTest {
    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private LikeService likeService;

    private Post post;
    private Member member;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        post = mock(Post.class);
        member = mock(Member.class);

        when(post.getId()).thenReturn(1L);
        when(member.getId()).thenReturn(1L);
    }

    @Test
    public void testLikePost() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(likeRepository.existsByPostIdAndMemberId(1L, 1L)).thenReturn(false);

        likeService.likePost(1L, 1L);

        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    public void testLikePostAlreadyLiked() {
        when(likeRepository.existsByPostIdAndMemberId(1L, 1L)).thenReturn(true);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            likeService.likePost(1L, 1L);
        });

        assertEquals("Already liked", exception.getMessage());
    }

    @Test
    public void testUnlikePost() {
        when(likeRepository.existsByPostIdAndMemberId(1L, 1L)).thenReturn(true);

        likeService.unlikePost(1L, 1L);

        verify(likeRepository, times(1)).deleteByPostIdAndMemberId(1L, 1L);
    }

    @Test
    public void testUnlikePostNotLiked() {
        when(likeRepository.existsByPostIdAndMemberId(1L, 1L)).thenReturn(false);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            likeService.unlikePost(1L, 1L);
        });

        assertEquals("Not liked yet", exception.getMessage());
    }

    @Test
    public void testGetLikeCount() {
        when(likeRepository.countByPostId(1L)).thenReturn(10L);

        long likeCount = likeService.getLikeCount(1L);

        assertEquals(10L, likeCount);
    }
}
