package com.localhub.localhub.IntegrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localhub.localhub.LocalhubApplication;
import com.localhub.localhub.config.TestExternalConfig;
import com.localhub.localhub.dto.request.RequestRestaurantDto;
import com.localhub.localhub.entity.UserEntity;
import com.localhub.localhub.entity.UserType;
import com.localhub.localhub.repository.jdbcReposi.RestaurantRepository;
import com.localhub.localhub.repository.jpaReposi.UserRepository;
import com.localhub.localhub.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(
        {TestExternalConfig.class,
        })
public class RestaurantTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RestaurantService restaurantService;

    @Autowired
    UserRepository userRepository;


    @MockitoBean
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockitoBean
    OAuth2UserRequest oAuth2UserRequest;



    @BeforeEach
    void setup() {

        UserEntity user = UserEntity.builder()
                .username("test")
                .name("test")
                .userType(UserType.OWNER)
                .build();
        userRepository.save(user);
    }


    @Test
    @WithMockUser(username = "test", roles = "USER")
    void 가게등록후_정상이면_200반환() throws Exception {

        //given

        RequestRestaurantDto request = new RequestRestaurantDto();

        request.setAddress("강서구");
        request.setName("테스트");
        request.setCategory("한식");


        //when & then

        mockMvc.perform(
                        post("/api/restaurant/save")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());

    }


}
