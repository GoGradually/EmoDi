package com.capstone.emodi.domain.privatekeyword;

import com.capstone.emodi.domain.privatepost.PrivatePost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivateKeywordRepository extends JpaRepository<PrivateKeyword, Long> {
    void deleteByPrivatePost(PrivatePost post);
}
