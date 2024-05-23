package com.capstone.emodi.service;

import com.capstone.emodi.domain.post.Post;
import com.capstone.emodi.domain.post.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class FeedService {
    private final PostRepository postRepository;

    public FeedService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Page<Post> getFriendFeed(Long memberId, Pageable pageable) {
        Page<Post> postsByFriends = postRepository.findRecentPostsByFriendsWithPaging(memberId, pageable);
        Page<Post> postsLikedByFriends = postRepository.findRecentPostsLikedByFriendsWithPaging(memberId, pageable);

        List<Post> mergedPosts = new ArrayList<>();
        mergedPosts.addAll(postsByFriends.getContent());
        mergedPosts.addAll(postsLikedByFriends.getContent());

        mergedPosts = mergedPosts.stream()
                .distinct()
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .collect(Collectors.toList());

        return new PageImpl<>(mergedPosts, pageable, postsByFriends.getTotalElements() + postsLikedByFriends.getTotalElements());
    }
}