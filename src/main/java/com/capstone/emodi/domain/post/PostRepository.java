package com.capstone.emodi.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
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
}