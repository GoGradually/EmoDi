package com.capstone.emodi.repository;
import com.capstone.emodi.domain.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findByMemberId(Long memberId);
    boolean existsByMemberIdAndFriendId(Long memberId, Long friendId);

    void deleteByMemberIdAndFriendId(Long memberId, Long friendId);
}

