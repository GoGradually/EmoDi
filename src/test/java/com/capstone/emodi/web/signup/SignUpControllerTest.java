package com.capstone.emodi.web.signup;

import com.capstone.emodi.web.SignUpController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SignUpControllerTest {

    @Autowired
    private MockMvc mockMvc;

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

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true));
    }

    @Test
    public void testSignUp_DuplicateLoginId() throws Exception {
        // given
        String loginId = "testuser";
        String password = "password";
        String username = "Test User";
        String email = "test@example.com";
        String tellNumber = "1234567890";

        String loginId2 = "testuser";
        String password2 = "password";
        String username2 = "Test User";
        String email2 = "test2@example.com";
        String tellNumber2 = "1234567890";

        String loginId3 = "testuser3";
        String password3 = "password";
        String username3 = "Test User";
        String email3 = "test@example.com";
        String tellNumber3 = "1234567890";

        SignUpController.SignupRequest signupRequest = new SignUpController.SignupRequest();
        signupRequest.setLoginId(loginId);
        signupRequest.setPassword(password);
        signupRequest.setUsername(username);
        signupRequest.setEmail(email);
        signupRequest.setTellNumber(tellNumber);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)));
        // when
        SignUpController.SignupRequest signupRequest2 = new SignUpController.SignupRequest();
        signupRequest2.setLoginId(loginId2);
        signupRequest2.setPassword(password2);
        signupRequest2.setUsername(username2);
        signupRequest2.setEmail(email2);
        signupRequest2.setTellNumber(tellNumber2);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest2)))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false));


        SignUpController.SignupRequest signupRequest3 = new SignUpController.SignupRequest();
        signupRequest3.setLoginId(loginId3);
        signupRequest3.setPassword(password3);
        signupRequest3.setUsername(username3);
        signupRequest3.setEmail(email3);
        signupRequest3.setTellNumber(tellNumber3);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest3)))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false));
    }
}