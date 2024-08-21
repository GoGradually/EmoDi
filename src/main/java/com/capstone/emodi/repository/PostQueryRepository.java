package com.capstone.emodi.repository;

import com.capstone.emodi.domain.*;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.capstone.emodi.domain.QFriendship.friendship;
import static com.capstone.emodi.domain.QMember.member;
import static com.capstone.emodi.domain.QPost.post;
import static com.querydsl.core.types.ExpressionUtils.count;
import static com.querydsl.jpa.JPAExpressions.select;

@RequiredArgsConstructor
@Repository
public class PostQueryRepository {

    private final JPAQueryFactory queryFactory;


    @Query("SELECT p FROM Post p WHERE p.member.id IN (SELECT f.friend.id FROM Friendship f WHERE f.member.id = :memberId)")
    Page<Post> findRecentPostsByFriendsWithPaging(@Param("memberId") Long memberId, Pageable pageable){
        Member member = queryFactory.selectFrom(QMember.member).where(QMember.member.id.eq(memberId)).fetchOne();

        // join 이 subQuery 보다 효율이 좋을까? 모르겠다 테스팅 필요
        // 전체 포스트를 다 가져오는건 좀 위험해보임
        List<Post> result = queryFactory
                .select(post)
                .from(post)
                .join(post.member, friendship.member)
                .where(friendship.friend.eq(member))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(count(post))
                .from(post)
                .join(post.member, friendship.member)
                .where(friendship.friend.eq(member));

        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }
//
//    @Query("SELECT p FROM Post p JOIN p.likes l WHERE l.member.id IN (SELECT f.friend.id FROM Friendship f WHERE f.member.id = :memberId)")
//    Page<Post> findRecentPostsLikedByFriendsWithPaging(@Param("memberId") Long memberId, Pageable pageable);
//    @Query("SELECT DISTINCT p FROM Post p " +
//            "WHERE p.member.id IN (SELECT f.friend.id FROM Friendship f WHERE f.member.id = :memberId) " +
//            "OR p.id IN (SELECT l.post.id FROM Like l WHERE l.member.id IN (SELECT f.friend.id FROM Friendship f WHERE f.member.id = :memberId)) " +
//            "ORDER BY p.createdAt DESC")
//    Page<Post> findRecentPostsAndLikedPostsByFriendsWithPagingWithMemberWithoutPrivatePosts(@Param("memberId") Long memberId, Pageable pageable);

}
