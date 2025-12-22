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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final RestaurantReviewRepository restaurantReviewRepository;


    public void save(String username, RequestRestaurantDto requestRestaurantDto) {

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));

        if (userEntity.getUserType() != UserType.OWNER) {
            throw new IllegalArgumentException("OWNER만 가게등록을 할 수 있습니다.");
        }


        int save = restaurantRepository.save(userEntity.getId(), requestRestaurantDto);
        if (save == 0) {
            throw new IllegalArgumentException("저장 실패");
        }

    }

    public void createReview(String username, CreateReview createReview) {

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));


        Restaurant restaurant = restaurantRepository.findById(createReview.getRestaurantId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 가게입니다."));

        if (restaurant.getOwnerId().equals(userEntity.getId())) {
            throw new IllegalArgumentException("자신의 가게에는 리뷰를 작성할 수 없습니다.");
        }

        int save = restaurantReviewRepository.save(userEntity.getId(), createReview);
        if (save != 1) {
            throw new RuntimeException("db 저장 실패");
        }

    }
}
