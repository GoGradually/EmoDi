package com.capstone.emodi.repository;

import com.capstone.emodi.domain.Member;
import com.capstone.emodi.domain.Post;
import com.capstone.emodi.domain.QMember;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.capstone.emodi.domain.QFriendship.friendship;
import static com.capstone.emodi.domain.QPost.post;
import static com.querydsl.core.types.ExpressionUtils.count;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByMemberId(Long memberId);
    List<Post> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Post> findByCreatedAtAfter(LocalDateTime date);
    List<Post> findByCreatedAtBefore(LocalDateTime date);
    List<Post> findByMemberIdAndCreatedAtBetween(Long memberId, LocalDateTime startOfDay, LocalDateTime endOfDay);

    @Query("SELECT p FROM Post p WHERE p.member.id IN (SELECT f.friend.id FROM Friendship f WHERE f.member.id = :memberId)")
    Page<Post> findRecentPostsByFriendsWithPaging(@Param("memberId") Long memberId, Pageable pageable);
    @Query("SELECT p FROM Post p JOIN p.likes l WHERE l.member.id IN (SELECT f.friend.id FROM Friendship f WHERE f.member.id = :memberId)")
    Page<Post> findRecentPostsLikedByFriendsWithPaging(@Param("memberId") Long memberId, Pageable pageable);
    @Query("SELECT DISTINCT p FROM Post p " +
            "WHERE p.member.id IN (SELECT f.friend.id FROM Friendship f WHERE f.member.id = :memberId) " +
            "OR p.id IN (SELECT l.post.id FROM Like l WHERE l.member.id IN (SELECT f.friend.id FROM Friendship f WHERE f.member.id = :memberId)) " +
            "ORDER BY p.createdAt DESC")
    Page<Post> findRecentPostsAndLikedPostsByFriendsWithPagingWithMemberWithoutPrivatePosts(@Param("memberId") Long memberId, Pageable pageable);

}