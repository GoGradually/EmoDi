package com.capstone.emodi.web.friendship;

import com.capstone.emodi.domain.friendship.Friendship;
import com.capstone.emodi.domain.member.Member;
import com.capstone.emodi.service.FriendshipService;
import com.capstone.emodi.web.dto.FriendshipDto;
import com.capstone.emodi.web.dto.MemberDto;
import com.capstone.emodi.web.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/friends")
public class FriendshipController {
    private final FriendshipService friendshipService;

    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    @PostMapping("/{memberId}/add/{friendId}")
    public ResponseEntity<ApiResponse<Void>> addFriend(@PathVariable Long memberId, @PathVariable Long friendId) {
        friendshipService.addFriend(memberId, friendId);
        return ResponseEntity.ok(ApiResponse.success("친구 추가 성공", null));
    }

    @DeleteMapping("/{memberId}/remove/{friendId}")
    public ResponseEntity<ApiResponse<Void>> removeFriend(@PathVariable Long memberId, @PathVariable Long friendId) {
        friendshipService.removeFriend(memberId, friendId);
        return ResponseEntity.ok(ApiResponse.success("친구 삭제 성공", null));
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<ApiResponse<List<MemberDto>>> getFriends(@PathVariable Long memberId) {
        List<Member> friends = friendshipService.getFriends(memberId);
        List<MemberDto> response = friends.stream().map(m -> new MemberDto(m, true)).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("친구 목록 조회 성공", response));
    }
}