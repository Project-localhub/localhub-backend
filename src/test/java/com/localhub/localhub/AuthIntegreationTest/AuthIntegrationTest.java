package com.localhub.localhub.AuthIntegreationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localhub.localhub.OAuth2.CustomOAuth2UserService;
import com.localhub.localhub.config.TestOAuthConfig;
import com.localhub.localhub.dto.request.ChangeTypeDto;
import com.localhub.localhub.entity.UserEntity;
import com.localhub.localhub.entity.UserRole;
import com.localhub.localhub.entity.UserType;
import com.localhub.localhub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestOAuthConfig.class)
public class AuthIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    CustomOAuth2UserService customOAuth2UserService;


    @Autowired
    ObjectMapper objectMapper;

    private Long savedUserId;

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
        savedUserId = user.getId();
    }

    @Test
    @WithMockUser(roles = "ADMIN" , username = "testUser")
    void 유저타입_변경시_성공_200반환() throws Exception {

        //given
        ChangeTypeDto changeTypeDto = new ChangeTypeDto();
        changeTypeDto.setChangeUserType(UserType.OWNER);

        //when & then
        mockMvc.perform(put("/api/user/changeUserType")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changeTypeDto))
        ).andExpect(status().is(200));

        UserEntity user = userRepository.findById(savedUserId).orElseThrow();
        assertThat(user.getUserType()).isEqualTo(UserType.OWNER);

    }

    @Test
    @WithMockUser(roles = "ADMIN",username = "testUser")
    void 유저타입_변경시_잘못된값_400반환() throws Exception {
        //CUSTOMER or OWNER 이외의 값.

    //given
        ChangeTypeDto changeTypeDto = new ChangeTypeDto();
        changeTypeDto.setChangeUserType(UserType.ERROR);

        //when & then
        mockMvc.perform(put("/api/user/changeUserType")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changeTypeDto))
        ).andExpect(status().is(400));
    }

    @Test
    @WithMockUser(roles = "ADMIN",username = "testUser")
    void 유저정보_확인() throws Exception {

        //when & then

        mockMvc.perform(get("/api/user/getUserInfo" ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"));

    }

    }





