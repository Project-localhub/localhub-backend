package com.localhub.localhub.AuthIntegreationTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.localhub.localhub.dto.request.JoinDto;
import com.localhub.localhub.dto.request.LoginRequest;
import com.localhub.localhub.entity.UserEntity;
import com.localhub.localhub.entity.UserRole;
import com.localhub.localhub.jwt.JWTUtil;
import com.localhub.localhub.repository.UserRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class JwtIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        UserEntity user = UserEntity.builder()
                .username("testUser")
                .password(bCryptPasswordEncoder.encode("1234"))
                .role(UserRole.USER)
                .build();

        userRepository.save(user);
    }


    @Test
    void post_로그인성공시_200반환_access토큰확인() throws Exception {


        // 1. DTO 생성
        LoginRequest request = new LoginRequest();
        request.setUsername("testUser");
        request.setPassword("1234");

        // 2. DTO → JSON 자동 변환
        String json = objectMapper.writeValueAsString(request);

        // 3. 요청 + access 헤더 검증
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(header().exists("access"));
    }


    @Test
    void post_로그인성공시_refresh_cookie담김() throws Exception {

        // given
        LoginRequest request = new LoginRequest();
        request.setUsername("testUser");
        request.setPassword("1234");


        // when
        ResultActions result = mockMvc.perform(
                post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(cookie().exists("refresh"))     // 쿠키 존재 확인
                .andExpect(cookie().value("refresh", Matchers.notNullValue()));
    }

    @Test
    void post_로그인_잘못된비밀번호면_401반환() throws Exception {

        //given

        LoginRequest wrongRequest = new LoginRequest();
        wrongRequest.setPassword(bCryptPasswordEncoder.encode("wrongpassword"));
        wrongRequest.setUsername("wrongusername");


        //when

        ResultActions result = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrongRequest))
        );

        //then

        result.andExpect(status().isUnauthorized());
    }

    @Test
    void post_로그인_존재하지않는아이디면_401반환() throws Exception {

        //given
        LoginRequest wrongRequest = new LoginRequest();
        wrongRequest.setUsername("wrongUsername");
        wrongRequest.setUsername(bCryptPasswordEncoder.encode("1234"));

        //when
        ResultActions result = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrongRequest))
        );

        //then
        result.andExpect(status().isUnauthorized());

    }

    @Test
    void post_회원가입성공시_200반환() throws Exception {
        //given

        JoinDto joinDto = new JoinDto();

        joinDto.setPassword(bCryptPasswordEncoder.encode("1243"));
        joinDto.setUsername("Join");

        //when

        ResultActions result = mockMvc.perform(post("/api/auth/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(joinDto)));

        //then
        result.andExpect(status().isOk());
    }

    @Test
    void post_회원가입시_이미존재하는_아이디면_400_반환() throws Exception {

        //given

        JoinDto joinDto = new JoinDto();
        joinDto.setUsername("testUser");
        joinDto.setPassword(bCryptPasswordEncoder.encode("1234"));

        //when

        mockMvc.perform(post("/api/auth/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinDto)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message")
                        .value("이미 존재하는 유저입니다."));
    }
}

