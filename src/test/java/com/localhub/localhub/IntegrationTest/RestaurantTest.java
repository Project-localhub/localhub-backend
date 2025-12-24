package com.localhub.localhub.IntegrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.localhub.localhub.config.TestExternalConfig;
import com.localhub.localhub.dto.request.CreateReview;
import com.localhub.localhub.dto.request.RequestRestaurantDto;
import com.localhub.localhub.dto.request.RequestRestaurantImages;
import com.localhub.localhub.entity.restaurant.*;
import com.localhub.localhub.repository.jdbcReposi.RestaurantReviewRepositoryJDBC;
import com.localhub.localhub.repository.jdbcReposi.RestaurantScoreRepositoryJDBC;
import com.localhub.localhub.repository.jpaReposi.RestaurantRepositoryJpa;
import com.localhub.localhub.entity.UserEntity;
import com.localhub.localhub.entity.UserType;
import com.localhub.localhub.repository.jdbcReposi.RestaurantRepositoryJDBC;
import com.localhub.localhub.repository.jdbcReposi.UserLikeRestaurantRepositoryJDBC;
import com.localhub.localhub.repository.jpaReposi.RestaurantImageRepositoryJpa;
import com.localhub.localhub.repository.jpaReposi.RestaurantKeywordRepositoryJpa;
import com.localhub.localhub.repository.jpaReposi.UserRepository;
import com.localhub.localhub.service.ImageUrlResolver;
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

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    UserLikeRestaurantRepositoryJDBC userLikeRestaurantRepositoryJDBC;
    @Autowired
    RestaurantImageRepositoryJpa restaurantImageRepositoryJpa;

    @Autowired
    RestaurantReviewRepositoryJDBC restaurantReviewRepositoryJDBC;


    @Autowired
    RestaurantScoreRepositoryJDBC restaurantScoreRepositoryJDBC;

    @MockitoBean
    ImageUrlResolver imageUrlResolver;

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

    @Autowired
    RestaurantKeywordRepositoryJpa restaurantKeywordRepositoryJpa;

    UserEntity user;
    UserEntity owner;
    Restaurant restaurant;

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

        restaurant = Restaurant.builder()
                .category(Category.한식)
                .address("테스트")
                .build();
        restaurantRepositoryJpa.save(restaurant);
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
    @WithMockUser(username = "test", roles = "USER")
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

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void 가게_찜하기_성공_200() throws Exception {

        //given
        Long userId = user.getId();
        String username = user.getUsername();
        Long restaurantId = restaurant.getId();

        //when
        mockMvc.perform(post("/api/restaurant/like/" + restaurantId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void 존재하지않는가게_400() throws Exception {

        //when & then
        mockMvc.perform(post("/api/restaurant/like/" + 999))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("존재하지않는 가게입니다."));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void 이미찜한가게_400() throws Exception {
        //given
        Long userId = user.getId();
        String userName = user.getName();
        Long restaurantId = restaurant.getId();
        userLikeRestaurantRepositoryJDBC.save(userId, restaurantId);

        //when & then
        mockMvc.perform(post("/api/restaurant/like/" + restaurantId))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("이미 찜한 가게입니다."));

    }

    @Test
    @WithMockUser(username = "owner", roles = "USER")
    void 이미지_값_잘들어갔는지_확인() throws Exception {

        //given
        RequestRestaurantDto request = new RequestRestaurantDto();

        RequestRestaurantImages images1 = new RequestRestaurantImages("www", 1);
        RequestRestaurantImages images2 = new RequestRestaurantImages("www", 2);
        RequestRestaurantImages images3 = new RequestRestaurantImages("www", 3);
        List<RequestRestaurantImages> imagesList = List.of(images1, images2, images3);
        request.setImages(imagesList);
        request.setAddress("강서구");
        request.setName("테스트");
        request.setCategory("한식");

        //when & then
        mockMvc.perform(post("/api/restaurant/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(200));
        List<RestaurantImages> savedImages = restaurantImageRepositoryJpa.findAll();
        assertThat(savedImages).hasSize(3);

    }

    @Test
    @WithMockUser(username = "owner", roles = "USER")
    void 키워드_값_잘들어갔는지_확인() throws Exception {


        //given
        RequestRestaurantDto request = new RequestRestaurantDto();
        request.setKeyword(List.of("키워드1", "키워드2", "키워드3"));
        request.setAddress("강서구");
        request.setName("테스트");
        request.setCategory("한식");

        //when & then
        mockMvc.perform(post("/api/restaurant/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(200));

        List<RestaurantKeyword> savedKeyword = restaurantKeywordRepositoryJpa.findAll();
        assertThat(savedKeyword).hasSize(3);
        assertThat(savedKeyword)
                .extracting(key -> key.getKeyword())
                .containsExactlyInAnyOrder("키워드1", "키워드2", "키워드3");
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void 레스토랑_조회_반환DTO값_정상확인() {

        // given

        Restaurant thisRestaurant = Restaurant.builder()
                .name("홍콩반점")
                .description("짬뽕 맛집")
                .businessNumber("123456789012")
                .address("서울")
                .phone("02-123-4567")
                .category(Category.한식)
                .latitude(new BigDecimal("37.1234567"))
                .longitude(new BigDecimal("127.1234567"))
                .openTime(LocalTime.of(9, 0))
                .closeTime(LocalTime.of(22, 0))
                .hasBreakTime(true)
                .breakStartTime(LocalTime.of(15, 0))
                .breakEndTime(LocalTime.of(16, 0))
                .build();

        Restaurant saveResult = restaurantRepositoryJpa.save(thisRestaurant);


        userLikeRestaurantRepositoryJDBC.save(user.getId(), saveResult.getId());


        for (int i = 0; i <= 1; i++) {
            restaurantImageRepositoryJpa.save(RestaurantImages.builder()
                    .restaurantId(saveResult.getId())
                    .imageKey("key" + i)
                    .build());
        }


        for (int i = 0; i <= 1; i++) {
            restaurantKeywordRepositoryJpa.save(
                    RestaurantKeyword.builder()
                            .restaurantId(saveResult.getId())
                            .keyword("keyword" + i)
                            .build()

            );
        }
        //when

        Restaurant result = restaurantRepositoryJpa.findById(saveResult.getId()).get();
        List<RestaurantKeyword> keywordList = restaurantKeywordRepositoryJpa.findByRestaurantId(saveResult.getId());
        List<RestaurantImages> imagesList = restaurantImageRepositoryJpa.findByRestaurantId(saveResult.getId());
        // then
        assertThat(result.getId()).isEqualTo(saveResult.getId());
        assertThat(result.getName()).isEqualTo("홍콩반점");
        assertThat(result.getDescription()).isEqualTo("짬뽕 맛집");
        assertThat(result.getCategory()).isEqualTo(Category.한식);

        assertThat(result.getOpenTime()).isEqualTo(LocalTime.of(9, 0));
        assertThat(result.getCloseTime()).isEqualTo(LocalTime.of(22, 0));
        assertThat(result.getHasBreakTime()).isTrue();


        //키워드 확인
        assertThat(keywordList)
                .extracting(key -> key.getKeyword())
                .containsExactlyInAnyOrder("keyword0", "keyword1");

        //이미지 확인
        assertThat(imagesList)
                .extracting(img -> img.getImageKey())
                .containsExactlyInAnyOrder("key0", "key1");

    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void 좋아요_조회_정상확인() throws Exception {


        // given

        Restaurant thisRestaurant = Restaurant.builder()
                .name("홍콩반점")
                .description("짬뽕 맛집")
                .businessNumber("123456789012")
                .address("서울")
                .phone("02-123-4567")
                .category(Category.한식)
                .latitude(new BigDecimal("37.1234567"))
                .longitude(new BigDecimal("127.1234567"))
                .openTime(LocalTime.of(9, 0))
                .closeTime(LocalTime.of(22, 0))
                .hasBreakTime(true)
                .breakStartTime(LocalTime.of(15, 0))
                .breakEndTime(LocalTime.of(16, 0))
                .build();

        Restaurant savedRestaurant = restaurantRepositoryJpa.save(thisRestaurant);

        userLikeRestaurantRepositoryJDBC.save(user.getId(), savedRestaurant.getId());


        for (int i = 0; i <= 1; i++) {
            restaurantImageRepositoryJpa.save(RestaurantImages.builder()
                    .restaurantId(savedRestaurant.getId())
                    .imageKey("key" + i)
                    .build());
        }


        for (int i = 0; i <= 1; i++) {
            restaurantKeywordRepositoryJpa.save(
                    RestaurantKeyword.builder()
                            .restaurantId(savedRestaurant.getId())
                            .keyword("keyword" + i)
                            .build()
            );
        }

        given(imageUrlResolver.toPresignedUrl(anyString()))
                .willReturn("https://mock-image-url");

        //when & then
        mockMvc.perform(get("/api/restaurant/" + savedRestaurant.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favoriteCount").value(1));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void 리뷰갯수_조회_정상확인() throws Exception {


        //given
        CreateReview createReview = new CreateReview();
        createReview.setRestaurantId(restaurant.getId());
        createReview.setScore(1);
        createReview.setContent("테스트");
        restaurantReviewRepositoryJDBC.save(user.getId(), createReview);


        //when & then
        mockMvc.perform(get("/api/restaurant/" + restaurant.getId()))
                .andExpect(jsonPath("$.reviewCount").value(1));


    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void 별점_평균계산_정상확인() throws Exception {

        //given

        restaurantScoreRepositoryJDBC.save(user.getId(), restaurant.getId(), 1);
        restaurantScoreRepositoryJDBC.save(owner.getId(), restaurant.getId(), 1);




        //when
        mockMvc.perform(get("/api/restaurant/" + restaurant.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(1.0));

    }



    @Test
    @WithMockUser(username = "user", roles = "USER")
    void 별점_평균계산_소수점_반환_확인() throws Exception {

        //given

        restaurantScoreRepositoryJDBC.save(user.getId(), restaurant.getId(), 1);
        restaurantScoreRepositoryJDBC.save(owner.getId(), restaurant.getId(), 2);




        //when
        mockMvc.perform(get("/api/restaurant/" + restaurant.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(1.5));

    }
}
