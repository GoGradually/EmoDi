package com.capstone.emodi;

import com.capstone.emodi.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class EmoDiApplicationTests {
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Test
    void contextLoads() {
        String accessToken = jwtTokenProvider.generateAccessToken("username");
        log.info("token = {}", accessToken);
    }

}
