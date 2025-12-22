package com.localhub.localhub.controller;

import com.localhub.localhub.dto.request.RequestRestaurantDto;
import com.localhub.localhub.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/restaurant")
public class RestaurantController {
    private final RestaurantService restaurantService;


    @Operation(summary = "가게 등록", description = "OWNER유저가 자신의 가게 등록")
    @PostMapping("/save")
    public ResponseEntity<String> saveRestaurant(Authentication authentication,
                                                 @RequestBody RequestRestaurantDto requestRestaurantDto) {

        restaurantService.save(authentication.getName(), requestRestaurantDto);
        return ResponseEntity.ok("가게 등록 완료.");
    }
}
