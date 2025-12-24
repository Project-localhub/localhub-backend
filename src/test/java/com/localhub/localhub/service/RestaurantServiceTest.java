package com.localhub.localhub.service;

import com.localhub.localhub.dto.request.CreateReview;
import com.localhub.localhub.dto.request.RequestRestaurantDto;
import com.localhub.localhub.entity.restaurant.Category;
import com.localhub.localhub.repository.jdbcReposi.RestaurantScoreRepositoryJDBC;
import com.localhub.localhub.repository.jpaReposi.RestaurantRepositoryJpa;
import com.localhub.localhub.entity.UserEntity;
import com.localhub.localhub.entity.UserType;
import com.localhub.localhub.entity.restaurant.Restaurant;
import com.localhub.localhub.entity.restaurant.RestaurantReview;
import com.localhub.localhub.repository.jdbcReposi.RestaurantRepositoryJDBC;
import com.localhub.localhub.repository.jdbcReposi.RestaurantReviewRepositoryJDBC;
import com.localhub.localhub.repository.jdbcReposi.UserLikeRestaurantRepositoryJDBC;
import com.localhub.localhub.repository.jpaReposi.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {


    @Mock
    UserLikeRestaurantRepositoryJDBC userLikeRestaurantRepositoryJDBC;
    @Mock
    RestaurantRepositoryJDBC restaurantRepositoryJDBC;
    @InjectMocks
    RestaurantService restaurantService;
    @Mock
    UserRepository userRepository;
    @Mock
    RestaurantRepositoryJpa restaurantRepository;
    @Mock
    RestaurantScoreRepositoryJDBC restaurantScoreRepositoryJDBC;

    @Mock
    RestaurantReviewRepositoryJDBC restaurantReviewRepositoryJDBC;
    UserEntity user;
    UserEntity owner;
    Restaurant testRestaurant;
    @BeforeEach
    void setUp() {

     user = UserEntity.builder()
                .id(1L)
                .userType(UserType.CUSTOMER)
                .username("test")
                .build();

        owner = UserEntity.builder()
                .id(2L)
                .userType(UserType.OWNER)
                .username("ownertest")
                .build();

        testRestaurant = Restaurant.builder()
                .id(1L)
                .ownerId(owner.getId())
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
        requestRestaurantDto.setImages(List.of());
        requestRestaurantDto.setCategory(Category.한식.name());

        given(userRepository.findByUsername(owner.getUsername()))
                .willReturn(Optional.of(owner));
        given(restaurantRepositoryJDBC.save(owner.getId(), requestRestaurantDto))
                .willReturn(3L);

        //when
        restaurantService.save(owner.getUsername(), requestRestaurantDto);

        //then
        verify(restaurantRepositoryJDBC).save(owner.getId(), requestRestaurantDto);
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
                .restaurant_id(restaurant.getId())
                .user_id(user.getId())
                .content("리뷰")
                .build();

        given(userRepository.findByUsername(user.getUsername()))
                .willReturn(Optional.of(user));
        given(restaurantRepositoryJDBC.findById(1L))
                .willReturn(Optional.of(restaurant));
        given(restaurantReviewRepositoryJDBC.save(user.getId(), createReview))
                .willReturn(1);
        //when
        restaurantService.createReview(user.getUsername(), createReview);
        //then
        verify(restaurantReviewRepositoryJDBC).save(user.getId(), createReview);
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
        given(restaurantRepositoryJDBC.findById(1L))
                .willReturn(Optional.of(restaurant));
        //when & then

        assertThatThrownBy(() -> restaurantService.createReview(user.getUsername(), createReview))
                .isInstanceOf(IllegalArgumentException.class);



    }

    @Test
    void 가게삭제_완료_db호출성공() {

        //given
        Long restaurantId = 1L;

        Restaurant restaurant = Restaurant.builder()
                .id(restaurantId)
                .ownerId(2L)
                .build();

        given(restaurantRepositoryJDBC.findById(restaurantId))
                .willReturn(Optional.of(restaurant));

        given(userRepository.findByUsername(owner.getUsername()))
                .willReturn(Optional.of(owner));
        given(restaurantRepositoryJDBC.deleteById(restaurantId))
                .willReturn(1);
        //when
        restaurantService.deleteRestaurant(owner.getUsername(), restaurantId);

        //then
        verify(restaurantRepositoryJDBC).deleteById(restaurantId);

    }

    @Test
    void 가게_OWNER가_아니면_에러발생() {

        //given
        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .ownerId(user.getId()) //위에 선언한 CUSTOMER의 아이디 선언.
                .build();
        Long restaurantId = restaurant.getId();

        given(restaurantRepositoryJDBC.findById(restaurantId))
                .willReturn(Optional.of(restaurant));

        given(userRepository.findByUsername(user.getUsername()))
                .willReturn(Optional.of(user));

        //when & then
        assertThatThrownBy(() -> restaurantService.deleteRestaurant(user.getUsername(), restaurantId))
                .hasMessageContaining("OWNER만 삭제할수있습니다.");
    }

    @Test
    void 이미_찜한가게면_에러발생() {

        //given
        given(userRepository.findByUsername(user.getUsername()))
                .willReturn(Optional.of(user));
        given(restaurantRepository.findById(testRestaurant.getId()))
                .willReturn(Optional.of(testRestaurant));
        given(userLikeRestaurantRepositoryJDBC.isExistByUserIdAndRestaurantId(user.getId(), testRestaurant.getId()))
                .willReturn(1);

        //when & then
        assertThatThrownBy(() -> restaurantService.likeRestaurant(user.getUsername(), testRestaurant.getId()))
                .hasMessageContaining("이미 찜한 가게입니다.");

    }


    @Test
    void 찜하기_성공_db호출() {

        //given
        given(userRepository.findByUsername(user.getUsername()))
                .willReturn(Optional.of(user));
        given(restaurantRepository.findById(testRestaurant.getId()))
                .willReturn(Optional.of(testRestaurant));
        given(userLikeRestaurantRepositoryJDBC.isExistByUserIdAndRestaurantId(user.getId(), testRestaurant.getId()))
                .willReturn(0);
        given(userLikeRestaurantRepositoryJDBC.save(user.getId(), testRestaurant.getId()))
                .willReturn(1);
        //when & then
        restaurantService.likeRestaurant(user.getUsername(),testRestaurant.getId());
        verify(userLikeRestaurantRepositoryJDBC).save(user.getId(), testRestaurant.getId());


    }
    @Test
    void 존재하지않는_가게_에러발생() {


        //given
        given(userRepository.findByUsername(user.getUsername()))
                .willReturn(Optional.of(user));
        given(restaurantRepository.findById(testRestaurant.getId()))
                .willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> restaurantService.likeRestaurant(user.getUsername(), testRestaurant.getId()))
                .hasMessageContaining("존재하지않는 가게입니다.");
    }


    @Test
    void 리뷰작성시_score값있으면_저장() {


        //given
        CreateReview createReview = new CreateReview();
        createReview.setContent("content");
        createReview.setRestaurantId(testRestaurant.getId());
        createReview.setScore(1);
        given(userRepository.findByUsername(user.getUsername()))
                .willReturn(Optional.of(user));
        given(restaurantRepositoryJDBC.findById(testRestaurant.getId()))
                .willReturn(Optional.of(testRestaurant));
        given(restaurantReviewRepositoryJDBC.save(user.getId(), createReview))
                .willReturn(1);
        //when
        restaurantService.createReview(user.getUsername(), createReview);

        //then
        verify(restaurantScoreRepositoryJDBC).save
    (user.getId(), testRestaurant.getId(), createReview.getScore());

    }

    @Test
    void 리뷰작성시_score값_없으면_scoreReposi_호출X() {

        //given
        CreateReview createReview = new CreateReview();
        createReview.setContent("content");
        createReview.setRestaurantId(testRestaurant.getId());
//        given(userRepository.findByUsername(user.getUsername()))
//                .willReturn(Optional.of(user));
//        given(restaurantRepositoryJDBC.findById(testRestaurant.getId()))
//                .willReturn(Optional.of(testRestaurant));
//        given(restaurantReviewRepository.save(user.getId(), createReview))
//                .willReturn(1);

        //when & then
        verify(restaurantScoreRepositoryJDBC, times(0))
                .save(anyLong(), anyLong(), anyInt());
    }

    @Test
    void score값_1에서5_범위_아닐시_에러발생_score6() {

        //given
        CreateReview createReview = new CreateReview();
        createReview.setContent("content");
        createReview.setRestaurantId(testRestaurant.getId());
        createReview.setScore(6);
        given(userRepository.findByUsername(user.getUsername()))
                .willReturn(Optional.of(user));
        given(restaurantRepositoryJDBC.findById(testRestaurant.getId()))
             .willReturn(Optional.of(testRestaurant));
        //when & then
        assertThatThrownBy(() -> restaurantService.createReview(user.getUsername(), createReview))
                .hasMessageContaining("별점은 1~5 사이여야 합니다.");

    }

    @Test
    void score값_1에서5_범위_아닐시_에러발생_score0() {

        //given
        CreateReview createReview = new CreateReview();
        createReview.setContent("content");
        createReview.setRestaurantId(testRestaurant.getId());
        createReview.setScore(0);
        given(userRepository.findByUsername(user.getUsername()))
                .willReturn(Optional.of(user));
        given(restaurantRepositoryJDBC.findById(testRestaurant.getId()))
                .willReturn(Optional.of(testRestaurant));
        //when & then
        assertThatThrownBy(() -> restaurantService.createReview(user.getUsername(), createReview))
                .hasMessageContaining("별점은 1~5 사이여야 합니다.");

    }
}

