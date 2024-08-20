package com.capstone.emodi.repository;

import com.capstone.emodi.domain.PrivatePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PrivatePostRepository extends JpaRepository<PrivatePost, Long> {

    List<PrivatePost> findByMemberId(Long memberId);
    List<PrivatePost> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<PrivatePost> findByCreatedAtAfter(LocalDateTime date);
    List<PrivatePost> findByCreatedAtBefore(LocalDateTime date);
    List<PrivatePost> findByMemberIdAndCreatedAtBetween(Long memberId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
