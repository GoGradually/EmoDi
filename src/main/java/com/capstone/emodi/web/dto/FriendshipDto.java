package com.capstone.emodi.web.dto;

import lombok.Data;

@Data
public class FriendshipDto {
    private Long memberId;
    private Long friendId;

    public FriendshipDto(Long memberId, Long friendId) {
        this.memberId = memberId;
        this.friendId = friendId;
    }
}
