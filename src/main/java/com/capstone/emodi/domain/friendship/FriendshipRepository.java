package com.capstone.emodi.domain.friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findByMemberId(Long memberId);
    boolean existsByMemberIdAndFriendId(Long memberId, Long friendId);

    void deleteByMemberIdAndFriendId(Long memberId, Long friendId);
}

