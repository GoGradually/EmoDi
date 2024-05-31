package com.capstone.emodi.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class FriendshipDto {
    private Long memberId;
    private Long friendId;

    public FriendshipDto(Long memberId, Long friendId) {
        this.memberId = memberId;
        this.friendId = friendId;
    }
}
