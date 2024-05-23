package com.capstone.emodi.web.auth;

import com.capstone.emodi.domain.session.LogoutTokenRepository;
import com.capstone.emodi.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
@Transactional
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LogoutTokenRepository logoutTokenRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String accessToken;
    private String refreshToken;
    @BeforeEach
    public void setup() {
        String loginId = "testUser";
        accessToken = jwtTokenProvider.generateAccessToken(loginId);
        refreshToken = jwtTokenProvider.generateRefreshToken(loginId);
    }


    @Test
    public void logoutTest() throws Exception {
        // 로그아웃 요청 보내기
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isOk())
                .andDo(print());

        // 로그아웃된 토큰으로 재발급 시도
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/refresh")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isUnauthorized())
                .andDo(print());

        // 리프레시 토큰이 블랙리스트에 추가되었는지 확인
        boolean isLoggedOut = logoutTokenRepository.isLoggedOut(refreshToken);
        assert isLoggedOut;
    }
}