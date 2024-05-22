package com.capstone.emodi.web.auth;

import com.capstone.emodi.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @WithMockUser
    public void testLogout_Success() throws Exception {
        // given
        String refreshToken = "valid_refresh_token";

        AuthController.LogoutRequest logoutRequest = new AuthController.LogoutRequest();
        logoutRequest.setRefreshToken(refreshToken);

        given(jwtTokenProvider.validateRefreshToken(refreshToken)).willReturn(true);
        given(jwtTokenProvider.isRefreshTokenValid(refreshToken)).willReturn(true);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // then
        verify(jwtTokenProvider, times(1)).removeRefreshToken(refreshToken);
    }

    @Test
    @WithMockUser
    public void testLogout_InvalidRefreshToken() throws Exception {
        // given
        String refreshToken = "invalid_refresh_token";

        AuthController.LogoutRequest logoutRequest = new AuthController.LogoutRequest();
        logoutRequest.setRefreshToken(refreshToken);

        given(jwtTokenProvider.validateRefreshToken(refreshToken)).willReturn(false);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Invalid refresh token"));
    }
}