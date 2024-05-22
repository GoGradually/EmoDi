package com.capstone.emodi.service;

import com.capstone.emodi.domain.like.Like;
import com.capstone.emodi.domain.like.LikeRepository;
import com.capstone.emodi.domain.member.Member;
import com.capstone.emodi.domain.member.MemberRepository;
import com.capstone.emodi.domain.post.Post;
import com.capstone.emodi.domain.post.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public LikeService(LikeRepository likeRepository, PostRepository postRepository, MemberRepository memberRepository) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    public void likePost(Long postId, Long memberId) {
        if (likeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw new IllegalStateException("Already liked");
        }
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));
        Like like = new Like(post, member);
        likeRepository.save(like);
    }

    public void unlikePost(Long postId, Long memberId) {
        if (!likeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw new IllegalStateException("Not liked yet");
        }
        likeRepository.deleteByPostIdAndMemberId(postId, memberId);
    }

    public long getLikeCount(Long postId) {
        return likeRepository.countByPostId(postId);
    }
}
