package com.localhub.localhub.AuthIntegreationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.localhub.localhub.LocalhubApplication;
import com.localhub.localhub.OAuth2.CustomOAuth2UserService;
import com.localhub.localhub.OAuth2.CustomSuccessHandler;
import com.localhub.localhub.config.TestOAuthConfig;
import com.localhub.localhub.dto.request.JoinDto;
import com.localhub.localhub.dto.request.LoginRequest;
import com.localhub.localhub.entity.RefreshEntity;
import com.localhub.localhub.entity.UserEntity;
import com.localhub.localhub.entity.UserRole;
import com.localhub.localhub.entity.UserType;
import com.localhub.localhub.jwt.JWTUtil;
import com.localhub.localhub.repository.RefreshRepository;
import com.localhub.localhub.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = LocalhubApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import(TestOAuthConfig.class)
public class JwtIntegrationTest {

    @Autowired
    RefreshRepository refreshRepository;

    @Autowired
    CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    CustomSuccessHandler customSuccessHandler;

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
                .userType(UserType.CUSTOMER)
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
        joinDto.setUserType(UserType.CUSTOMER);

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
        joinDto.setUserType(UserType.CUSTOMER);

        //when

        mockMvc.perform(post("/api/auth/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinDto)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message")
                        .value("이미 존재하는 유저입니다."));
    }



    @Test
    @DisplayName("AccessToken 없이 요청 시 401 또는 403 반환")
    void get_access_no_token_401_or_403() throws Exception {

        mockMvc.perform(get("/forTest"))
                .andExpect(status().is4xxClientError());  // 401 또는 403
    }

    @Test
    @DisplayName("만료된 AccessToken 사용 시 401 반환")
    void get_access_expired_401() throws Exception {

        // given
        String expiredAccess = jwtUtil.createJwt("access", "testuser", "ROLE_USER", -1L);

        // when & then
        mockMvc.perform(get("/forTest")
                        .header("Authorization", "Bearer " + expiredAccess))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @DisplayName("정상 refresh 요청 시 새 access 반환(통합테스트)")
    void post_refresh_정상refresh요청시_새access반환() throws Exception {

        // given — 실제 refresh 토큰 생성
        String refresh = jwtUtil.createJwt(
                "refresh",
                "testuser",
                "ROLE_USER",
                86400000L
        );

        // DB에 refresh 저장 (통합테스트 핵심)

        RefreshEntity refreshEntity = RefreshEntity.builder()
                .expiration("86400000L")
                .refresh(refresh)
                .username("testuser")
                .build();

        refreshRepository.save(refreshEntity);

        // when & then — 실제 서비스 호출
        mockMvc.perform(post("/reissue")
                        .cookie(new Cookie("refresh", refresh)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(cookie().exists("refresh"));
    }

    @Test
    @DisplayName("category 오류 refresh 요청 시 400 반환")
    void post_refresh_category오류_refresh요청시_400반환() throws Exception {

        // given — category가 'access' 인 잘못된 refresh 토큰 생성
        String wrongRefresh = jwtUtil.createJwt(
                "access",           // refresh가 아님
                "testuser",
                "ROLE_USER",
                86400000L
        );

        // DB에는 정상 refresh 토큰을 저장 (서비스 로직에서 DB 조회까지는 정상적으로 진행됨)
        RefreshEntity refreshEntity = RefreshEntity.builder()
                .username("testuser")
                .refresh(wrongRefresh)      // 일부러 같은 잘못된 토큰을 저장해도 상관 없음
                .expiration("86400000")
                .build();

        refreshRepository.save(refreshEntity);

        // when & then
        mockMvc.perform(post("/reissue")
                        .cookie(new Cookie("refresh", wrongRefresh)))
                .andExpect(status().isBadRequest())  // 400
                .andExpect(jsonPath("$.message").value("invalid refresh token"));
    }



}

