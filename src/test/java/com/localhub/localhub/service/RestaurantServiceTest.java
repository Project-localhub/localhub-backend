package com.localhub.localhub.service;

import com.localhub.localhub.dto.request.RequestRestaurantDto;
import com.localhub.localhub.entity.UserEntity;
import com.localhub.localhub.entity.UserType;
import com.localhub.localhub.repository.jdbcReposi.RestaurantRepository;
import com.localhub.localhub.repository.jpaReposi.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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

@ExtendWith(MockitoExtension.class)

class RestaurantServiceTest {


    @Mock
    RestaurantRepository restaurantRepository;
    @InjectMocks
    RestaurantService restaurantService;
    @Mock
    UserRepository userRepository;


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
    void 유저가_존재하지않는_경우_에러발생() {

        //gvien

        RequestRestaurantDto requestRestaurantDto = new RequestRestaurantDto();
        UserEntity user = UserEntity.builder()
                .id(1L)
                .username("테스트")
                .userType(UserType.CUSTOMER)
                .build();

        given(userRepository.findByUsername(user.getUsername()))
                .willReturn(Optional.empty());


        //when & then

        assertThatThrownBy(() -> restaurantService.save(user.getName(), requestRestaurantDto))
                .isInstanceOf(EntityNotFoundException.class);

    }

}