package com.localhub.localhub.service;


import com.localhub.localhub.dto.request.RequestRestaurantDto;
import com.localhub.localhub.entity.UserEntity;
import com.localhub.localhub.entity.UserType;
import com.localhub.localhub.repository.jdbcReposi.RestaurantRepository;
import com.localhub.localhub.repository.jpaReposi.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

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



}
