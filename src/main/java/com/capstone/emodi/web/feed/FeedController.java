package com.capstone.emodi.web.feed;
import com.capstone.emodi.domain.post.Post;
import com.capstone.emodi.service.FeedService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FeedController {
    private final FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping("/feed")
    public ResponseEntity<List<Post>> getFriendFeed(@RequestParam Long memberId) {
        List<Post> feed = feedService.getFriendFeed(memberId);
        return ResponseEntity.ok(feed);
    }
}

