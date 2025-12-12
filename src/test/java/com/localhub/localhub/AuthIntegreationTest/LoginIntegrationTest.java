package com.localhub.localhub.AuthIntegreationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.localhub.localhub.entity.UserEntity;
import com.localhub.localhub.entity.UserRole;
import com.localhub.localhub.entity.UserType;
import com.localhub.localhub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.localhub.localhub.config.TestOAuthConfig;
import com.localhub.localhub.dto.request.LoginRequest;
import com.localhub.localhub.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Import(TestOAuthConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LoginIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(LoginIntegrationTest.class);
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {

        UserEntity user = UserEntity.builder()
                .email("test@test.com")
                .password(bCryptPasswordEncoder.encode("1234"))
                .role(UserRole.USER)
                .userType(UserType.CUSTOMER)
                .phone("01012345678")
                .username("test")
                .build();

        userRepository.save(user);
    }


    @Test
    void post_로그인_정상요청시_200반환() throws Exception {

        //given

        LoginRequest request = new LoginRequest();
        request.setUsername("test");
        request.setPassword("1234");

        //when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());


    }

    @Test
    void post_로그인_성공시_accessToken_body로_반환() throws Exception {
        // given
        LoginRequest request = new LoginRequest();
        request.setUsername("test");
        request.setPassword("1234");
        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.accessToken").isString());
    }


    @Test
    void post_로그인_성공시_refreshToken_쿠키로_반환() throws Exception {

        // given
        LoginRequest request = new LoginRequest();
        request.setUsername("test");
        request.setPassword("1234");

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("refresh"))     //
                .andExpect(cookie().httpOnly("refresh", true));
    }

    @Test
    void post_로그인_존재하지않는아이디_400반환() throws Exception {
        //given

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword("1234");
        loginRequest.setPassword("wrong");

        //when&then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is(404));

    }
}
