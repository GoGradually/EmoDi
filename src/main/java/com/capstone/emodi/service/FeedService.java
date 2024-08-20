package com.capstone.emodi.service;

import com.capstone.emodi.repository.PostRepository;
import com.capstone.emodi.dto.PostDto;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FeedService {
    private final PostRepository postRepository;

    public FeedService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Page<PostDto> getFriendFeed(Long memberId, Pageable pageable) {
        try {
            List<PostDto> mergedPosts = postRepository.findRecentPostsAndLikedPostsByFriendsWithPagingWithMemberWithoutPrivatePosts(memberId, pageable)
                    .stream()
                    .distinct()
                    .map(PostDto::new)
                    .collect(Collectors.toList());


            return new PageImpl<>(mergedPosts, pageable, mergedPosts.size());
        } catch (Exception e) {
            // 예외 처리 로직 추가
            // 예외 로깅, 사용자에게 적절한 메시지 반환 등
            throw new RuntimeException("Failed to retrieve friend feed", e);
        }
    }
}
