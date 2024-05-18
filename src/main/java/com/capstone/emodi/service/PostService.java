// PostService.java
package com.capstone.emodi.service;

import com.capstone.emodi.domain.member.Member;
import com.capstone.emodi.domain.post.Post;
import com.capstone.emodi.domain.post.PostRepository;
import com.capstone.emodi.exception.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    // 게시글 작성
    public Post createPost(String title, String content, String imagePath, Member member) {
        Post post = Post.builder()
                .title(title)
                .content(content)
                .imagePath(imagePath)
                .member(member)
                .build();
        return postRepository.save(post);
    }

    // 게시글 수정
    public Post updatePost(Long postId, String title, String content, String imagePath) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("해당 게시글이 없습니다. id=" + postId));
        post.update(title, content, imagePath);
        return post;
    }

    // 게시글 삭제
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("해당 게시글이 없습니다. id=" + postId));
        postRepository.delete(post);
    }

    // 특정 회원이 작성한 게시글 목록 조회
    public List<Post> getPostsByMemberId(Long memberId) {
        return postRepository.findByMemberId(memberId);
    }

    // 특정 기간 내에 작성된 게시글 목록 조회
    public List<Post> getPostsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return postRepository.findByCreatedAtBetween(startDate, endDate);
    }

    // 특정 날짜 이후에 작성된 게시글 목록 조회
    public List<Post> getPostsAfterDate(LocalDateTime date) {
        return postRepository.findByCreatedAtAfter(date);
    }

    // 특정 날짜 이전에 작성된 게시글 목록 조회
    public List<Post> getPostsBeforeDate(LocalDateTime date) {
        return postRepository.findByCreatedAtBefore(date);
    }

    public List<Post> getPostsByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        return postRepository.findByCreatedAtBetween(startOfDay, endOfDay);
    }

    // 특정 사용자가 특정 날짜에 작성한 게시글 목록 조회
    public List<Post> getPostsByMemberIdAndDate(Long memberId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        return postRepository.findByMemberIdAndCreatedAtBetween(memberId, startOfDay, endOfDay);
    }
}