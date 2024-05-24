package com.capstone.emodi.web.signup;

import com.capstone.emodi.domain.member.MemberRepository;
import com.capstone.emodi.security.JwtTokenProvider;
import com.capstone.emodi.service.SignUpService;
import com.capstone.emodi.web.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SignUpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SignUpService signUpService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testSignUp_Success() throws Exception {
        // given
        String loginId = "testuser";
        String password = "password";
        String username = "Test User";
        String email = "test@example.com";
        String tellNumber = "1234567890";

        SignUpController.SignupRequest signupRequest = new SignUpController.SignupRequest();
        signupRequest.setLoginId(loginId);
        signupRequest.setPassword(password);
        signupRequest.setUsername(username);
        signupRequest.setEmail(email);
        signupRequest.setTellNumber(tellNumber);

        String accessToken = "access_token";
        String refreshToken = "refresh_token";

        given(jwtTokenProvider.generateAccessToken(loginId)).willReturn(accessToken);
        given(jwtTokenProvider.generateRefreshToken(loginId)).willReturn(refreshToken);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        given(signUpService.signUp(any(SignUpController.SignupRequest.class))).willReturn(tokens);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.accessToken").value(accessToken))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.refreshToken").value(refreshToken));
    }

    @Test
    public void testSignUp_DuplicateLoginId() throws Exception {
        // given
        String loginId = "testuser";
        String password = "password";
        String username = "Test User";
        String email = "test@example.com";
        String tellNumber = "1234567890";

        SignUpController.SignupRequest signupRequest = new SignUpController.SignupRequest();
        signupRequest.setLoginId(loginId);
        signupRequest.setPassword(password);
        signupRequest.setUsername(username);
        signupRequest.setEmail(email);
        signupRequest.setTellNumber(tellNumber);

        given(signUpService.signUp(any(SignUpController.SignupRequest.class))).willThrow(new IllegalArgumentException("이미 존재하는 아이디입니다."));

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("이미 존재하는 아이디입니다."));
    }
}