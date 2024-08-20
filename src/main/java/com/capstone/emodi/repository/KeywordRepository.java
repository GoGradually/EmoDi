package com.capstone.emodi.repository;

import com.capstone.emodi.domain.Keyword;
import com.capstone.emodi.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    void deleteByPost(Post post);
}
