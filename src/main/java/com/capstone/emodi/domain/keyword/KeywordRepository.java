package com.capstone.emodi.domain.keyword;

import com.capstone.emodi.domain.member.Member;
import com.capstone.emodi.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    void deleteByPost(Post post);
}
