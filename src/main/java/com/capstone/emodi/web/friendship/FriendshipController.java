package com.capstone.emodi.web.friendship;
import com.capstone.emodi.domain.friendship.Friendship;
import com.capstone.emodi.service.FriendshipService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
public class FriendshipController {
    private final FriendshipService friendshipService;

    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    @PostMapping("/{memberId}/add/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable Long memberId, @PathVariable Long friendId) {
        friendshipService.addFriend(memberId, friendId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{memberId}/remove/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable Long memberId, @PathVariable Long friendId) {
        friendshipService.removeFriend(memberId, friendId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<List<Friendship>> getFriends(@PathVariable Long memberId) {
        List<Friendship> friends = friendshipService.getFriends(memberId);
        return ResponseEntity.ok(friends);
    }
}
