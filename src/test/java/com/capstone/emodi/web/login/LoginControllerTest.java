package com.capstone.emodi.web.login;

import com.capstone.emodi.repository.MemberRepository;
import com.capstone.emodi.security.JwtTokenProvider;
import com.capstone.emodi.service.LoginService;
import com.capstone.emodi.web.LoginController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.BDDMockito.given;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoginService loginService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testLogin_Success() throws Exception {
        // given
        String loginId = "testuser";
        String password = "password";

        String accessToken = "access_token";
        String refreshToken = "refresh_token";

        given(jwtTokenProvider.generateAccessToken(loginId)).willReturn(accessToken);
        given(jwtTokenProvider.generateRefreshToken(loginId)).willReturn(refreshToken);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        LoginController.LoginRequest loginRequest = new LoginController.LoginRequest();
        loginRequest.setLoginId(loginId);
        loginRequest.setPassword(password);

        given(loginService.login(loginId, password)).willReturn(tokens);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.accessToken").value(accessToken))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.refreshToken").value(refreshToken));
    }

    @Test
    public void testLogin_InvalidCredentials() throws Exception {
        // given
        String loginId = "testuser";
        String password = "password";

        LoginController.LoginRequest loginRequest = new LoginController.LoginRequest();
        loginRequest.setLoginId(loginId);
        loginRequest.setPassword(password);

        given(loginService.login(loginId, password)).willThrow(new IllegalArgumentException("가입되지 않은 아이디이거나 잘못된 비밀번호입니다."));

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("가입되지 않은 아이디이거나 잘못된 비밀번호입니다."));
    }

}