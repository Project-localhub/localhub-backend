package com.localhub.localhub.service;


import com.localhub.localhub.dto.request.CreateReview;
import com.localhub.localhub.dto.request.RequestRestaurantDto;
import com.localhub.localhub.dto.response.ResponseRestaurantDto;
import com.localhub.localhub.entity.RestaurantRepositoryJpa;
import com.localhub.localhub.entity.UserEntity;
import com.localhub.localhub.entity.UserType;
import com.localhub.localhub.entity.restaurant.Restaurant;
import com.localhub.localhub.entity.restaurant.UserLikeRestaurant;
import com.localhub.localhub.repository.jdbcReposi.RestaurantRepositoryJDBC;
import com.localhub.localhub.repository.jdbcReposi.RestaurantReviewRepository;
import com.localhub.localhub.repository.jdbcReposi.UserLikeRestaurantRepositoryJDBC;
import com.localhub.localhub.repository.jpaReposi.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RestaurantService {
    private final RestaurantRepositoryJpa restaurantRepositoryJpa;
    private final RestaurantRepositoryJDBC restaurantRepositoryJDBC;
    private final UserRepository userRepository;
    private final RestaurantReviewRepository restaurantReviewRepository;
    private final UserLikeRestaurantRepositoryJDBC userLikeRestaurantRepositoryJDBC;


    //가게 등록
    public void save(String username, RequestRestaurantDto requestRestaurantDto) {

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));

        if (userEntity.getUserType() != UserType.OWNER) {
            throw new IllegalArgumentException("OWNER만 가게등록을 할 수 있습니다.");
        }


        int save = restaurantRepositoryJDBC.save(userEntity.getId(), requestRestaurantDto);
        if (save == 0) {
            throw new IllegalArgumentException("저장 실패");
        }


    }

    //가게 정보 수정
    @Transactional
    public void updateRestaurantInfo(String username, RequestRestaurantDto dto) {


        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));


        Restaurant restaurant = restaurantRepositoryJDBC.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("가게를 찾을 수 없습니다."));


        if (userEntity.getId().equals(restaurant.getOwnerId())) {
            throw new IllegalArgumentException("가게 주인만 수정이 가능합니다.");
        }
        restaurant.update(dto);
    }

    //가게 정보 조회
    public ResponseRestaurantDto findRestaurantById(Long restaurantId) {
        Restaurant restaurant = restaurantRepositoryJDBC.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("가게정보를 찾을 수 없습니다."));

        ResponseRestaurantDto build = ResponseRestaurantDto.builder()
                .id(restaurantId)
                .description(restaurant.getDescription())
                .businessNumber(restaurant.getBusinessNumber())
                .breakEndTime(restaurant.getBreakEndTime())
                .breakStartTime(restaurant.getBreakStartTime())
                .name(restaurant.getName())
                .address(restaurant.getAddress())
                .phone(restaurant.getPhone())
                .keyword(List.of())
                .imageUrl(List.of())
                .category(restaurant.getCategory().name())
                .latitude(restaurant.getLatitude())
                .longitude(restaurant.getLongitude())
                .openTime(restaurant.getOpenTime())
                .closeTime(restaurant.getCloseTime())
                .hasBreakTime(restaurant.getHasBreakTime())
                .favoriteCount(0) //추후수정
                .reviewCount(0) //추후수정
                .build();


        return build;
    }

    //가게 삭제
    public void deleteRestaurant(String username, Long restaurantId) {

        Restaurant restaurant = restaurantRepositoryJDBC.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("가게를 찾을 수 없습니다."));

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

        if (!restaurant.getOwnerId().equals(userEntity.getId())) {
            throw new IllegalArgumentException("가게 주인만 정보를 지울 수 있습니다.");
        }


        if (userEntity.getUserType() != UserType.OWNER) {
            throw new IllegalArgumentException("OWNER만 삭제할수있습니다.");
        }

        int result = restaurantRepositoryJDBC.deleteById(restaurantId);
        if (result == 0) {
            throw new RuntimeException("삭제 실패 서버오류발생");
        }
    }



    //리뷰작성
    public void createReview(String username, CreateReview createReview) {

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));


        Restaurant restaurant = restaurantRepositoryJDBC.findById(createReview.getRestaurantId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 가게입니다."));

        if (restaurant.getOwnerId().equals(userEntity.getId())) {
            throw new IllegalArgumentException("자신의 가게에는 리뷰를 작성할 수 없습니다.");
        }

        int save = restaurantReviewRepository.save(userEntity.getId(), createReview);
        if (save != 1) {
            throw new RuntimeException("db 저장 실패");
        }
    }

    //가게 찜하기
    public void likeRestaurant(String username, Long restaurantId) {

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

        Restaurant restaurant = restaurantRepositoryJpa.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지않는 가게입니다."));

        int isExist =  userLikeRestaurantRepositoryJDBC.isExistByUserIdAndRestaurantId
                        (userEntity.getId(), restaurantId);
        //이미 찜한가게면 에러발생
        if (isExist == 1) {
            throw new IllegalArgumentException("이미 찜한 가게입니다.");
        }
        //가게저장
        int result = userLikeRestaurantRepositoryJDBC.save(userEntity.getId(), restaurantId);
        if (result != 1) {
            throw new RuntimeException("db 저장 실패");
        }
    }


}
