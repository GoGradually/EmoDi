package com.capstone.emodi.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByMemberId(Long memberId);
    List<Post> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Post> findByCreatedAtAfter(LocalDateTime date);
    List<Post> findByCreatedAtBefore(LocalDateTime date);
    List<Post> findByMemberIdAndCreatedAtBetween(Long memberId, LocalDateTime startOfDay, LocalDateTime endOfDay);

    @Query("SELECT p FROM Post p WHERE p.member.id IN (SELECT f.friend.id FROM Friendship f WHERE f.member.id = :memberId) AND p.createdAt >= :since")
    List<Post> findRecentPostsByFriends(@Param("memberId") Long memberId, @Param("since") LocalDateTime since);

    @Query("SELECT p FROM Post p JOIN p.likes l WHERE l.member.id IN (SELECT f.friend.id FROM Friendship f WHERE f.member.id = :memberId) AND p.createdAt >= :since")
    List<Post> findRecentPostsLikedByFriends(@Param("memberId") Long memberId, @Param("since") LocalDateTime since);

}