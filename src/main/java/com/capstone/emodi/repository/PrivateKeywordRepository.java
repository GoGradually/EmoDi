package com.capstone.emodi.repository;

import com.capstone.emodi.domain.PrivateKeyword;
import com.capstone.emodi.domain.PrivatePost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivateKeywordRepository extends JpaRepository<PrivateKeyword, Long> {
    void deleteByPrivatePost(PrivatePost post);
}
