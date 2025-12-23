package com.localhub.localhub.IntegrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localhub.localhub.config.TestExternalConfig;
import com.localhub.localhub.config.TestSecurityConfig;
import com.localhub.localhub.dto.request.CreateReview;
import com.localhub.localhub.dto.request.RequestRestaurantDto;
import com.localhub.localhub.entity.RestaurantRepositoryJpa;
import com.localhub.localhub.entity.UserEntity;
import com.localhub.localhub.entity.UserType;
import com.localhub.localhub.entity.restaurant.Restaurant;
import com.localhub.localhub.repository.jdbcReposi.RestaurantRepositoryJDBC;
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
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(
        {TestExternalConfig.class
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

    @Autowired
    RestaurantRepositoryJDBC restaurantRepositoryJDBC;

    @Autowired
    RestaurantRepositoryJpa restaurantRepositoryJpa;

    @MockitoBean
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockitoBean
    OAuth2UserRequest oAuth2UserRequest;


    UserEntity user;
    UserEntity owner;

    @BeforeEach
    void setup() {

        user = UserEntity.builder()
                .username("user")
                .name("user")
                .userType(UserType.CUSTOMER)
                .build();
        userRepository.save(user);



        owner = UserEntity.builder()
                .username("owner")
                .name("owner")
                .userType(UserType.OWNER)
                .build();
        userRepository.save(owner);


    }


    @Test
    @WithMockUser(username = "owner", roles = "USER")
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

    @Test
    @WithMockUser(username = "wrong", roles = "USER")
    void 가게등록_존재하지않는유저_404반환() throws Exception {
        //given

        RequestRestaurantDto request = new RequestRestaurantDto();

        request.setAddress("강서구");
        request.setName("테스트");
        request.setCategory("한식");


        //when&then
        mockMvc.perform(post("/api/restaurant/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().is(404));

    }

    @Test
    @WithMockUser(username = "test" , roles = "USER")
    void 가게_리뷰_작성_성공_200() {


        //given
        RestaurantRepositoryJDBC restaurantRepositoryJDBC;


        CreateReview createReview = new CreateReview();
        createReview.setRestaurantId(1L);
        createReview.setContent("테스트");



    }


    @Test
    @WithMockUser(username = "owner", roles = "USER")
    void 가게_삭제_성공_200() throws Exception {

        //given

        Restaurant restaurant = Restaurant.builder()
                .ownerId(owner.getId())
                .build();
        restaurant = restaurantRepositoryJpa.save(restaurant);
        Long restaurantId = restaurant.getId();

        //when & then
        mockMvc.perform(delete("/api/restaurant/delete/" + restaurantId))
                .andExpect(status().is(200));

    }
}
