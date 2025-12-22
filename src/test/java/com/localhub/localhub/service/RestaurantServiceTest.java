package com.localhub.localhub.service;

import com.localhub.localhub.dto.request.CreateReview;
import com.localhub.localhub.dto.request.RequestRestaurantDto;
import com.localhub.localhub.entity.UserEntity;
import com.localhub.localhub.entity.UserType;
import com.localhub.localhub.entity.restaurant.Restaurant;
import com.localhub.localhub.entity.restaurant.RestaurantReview;
import com.localhub.localhub.repository.jdbcReposi.RestaurantRepository;
import com.localhub.localhub.repository.jdbcReposi.RestaurantReviewRepository;
import com.localhub.localhub.repository.jpaReposi.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {


    @Mock
    RestaurantRepository restaurantRepository;
    @InjectMocks
    RestaurantService restaurantService;
    @Mock
    UserRepository userRepository;

    @Mock
    RestaurantReviewRepository restaurantReviewRepository;

    UserEntity user;

    @BeforeEach
    void setUp() {

     user = UserEntity.builder()
                .id(1L)
                .userType(UserType.CUSTOMER)
                .username("test")
                .build();

    }

    @Test
    void 가게등록시_ONWER가_아니면_에러발생() {

        //gvien

        RequestRestaurantDto requestRestaurantDto = new RequestRestaurantDto();
        UserEntity user = UserEntity.builder()
                .id(1L)
                .username("테스트")
                .userType(UserType.CUSTOMER)
                .build();

        given(userRepository.findByUsername(user.getUsername()))
                .willReturn(Optional.of(user));


        //when & then

        assertThatThrownBy(() -> restaurantService.save(user.getUsername(), requestRestaurantDto))
                .isInstanceOf(IllegalArgumentException.class);


    }

    @Test
    void 가게등록_유저가_존재하지않는_경우_에러발생() {

        //given

        RequestRestaurantDto requestRestaurantDto = new RequestRestaurantDto();
        UserEntity user2 = UserEntity.builder()
                .id(2L)
                .username("테스트2")
                .userType(UserType.CUSTOMER)
                .build();

        given(userRepository.findByUsername(user.getUsername()))
                .willReturn(Optional.empty());


        //when & then

        assertThatThrownBy(() -> restaurantService.save(user.getUsername(), requestRestaurantDto))
                .isInstanceOf(EntityNotFoundException.class);

    }

    @Test
    void save_int1반환_정상호출_검증() {

        //given
        RequestRestaurantDto requestRestaurantDto = new RequestRestaurantDto();
        UserEntity user = UserEntity.builder()
                .id(1L)
                .username("테스트")
                .userType(UserType.OWNER)
                .build();

        given(userRepository.findByUsername(user.getUsername()))
                .willReturn(Optional.of(user));
        given(restaurantRepository.save(user.getId(), requestRestaurantDto))
                .willReturn(1);

        //when
        restaurantService.save(user.getUsername(), requestRestaurantDto);

        //then
        verify(restaurantRepository).save(user.getId(), requestRestaurantDto);
    }

    @Test
    void 레스토랑리뷰_저장_db호출성공() {

        //given
        CreateReview createReview = new CreateReview();
        createReview.setRestaurantId(1L);
        createReview.setContent("리뷰");

        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .ownerId(2L)
                .build();

        RestaurantReview restaurantReview = RestaurantReview.builder()
                .restaurant_id(1L)
                .user_id(2L)
                .content("리뷰")
                .build();

        given(userRepository.findByUsername(user.getUsername()))
                .willReturn(Optional.of(user));
        given(restaurantRepository.findById(1L))
                .willReturn(Optional.of(restaurant));
        given(restaurantReviewRepository.save(user.getId(), createReview))
                .willReturn(1);
        //when
        restaurantService.createReview(user.getUsername(), createReview);
        //then
        verify(restaurantReviewRepository).save(user.getId(), createReview);
    }

    @Test
    void 레스트로랑리뷰_같은유저면_에러발생() {


        //given
        CreateReview createReview = new CreateReview();
        createReview.setRestaurantId(1L);
        createReview.setContent("리뷰");

        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .ownerId(1L)
                .build();

        RestaurantReview restaurantReview = RestaurantReview.builder()
                .restaurant_id(1L)
                .user_id(1L)
                .content("리뷰")
                .build();

        given(userRepository.findByUsername(user.getUsername()))
                .willReturn(Optional.of(user));
        given(restaurantRepository.findById(1L))
                .willReturn(Optional.of(restaurant));
        //when & then

        assertThatThrownBy(() -> restaurantService.createReview(user.getUsername(), createReview))
                .isInstanceOf(IllegalArgumentException.class);



    }




}

