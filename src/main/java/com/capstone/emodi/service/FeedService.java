package com.capstone.emodi.service;

import com.capstone.emodi.domain.post.Post;
import com.capstone.emodi.domain.post.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public List<Post> getFriendFeed(Long memberId) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        List<Post> postsByFriends = postRepository.findRecentPostsByFriends(memberId, oneWeekAgo);
        List<Post> postsLikedByFriends = postRepository.findRecentPostsLikedByFriends(memberId, oneWeekAgo);

        return Stream.concat(postsByFriends.stream(), postsLikedByFriends.stream())
                .distinct()
                .collect(Collectors.toList());
    }
}