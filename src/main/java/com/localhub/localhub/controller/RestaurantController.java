package com.localhub.localhub.controller;

import com.localhub.localhub.dto.request.CreateReview;
import com.localhub.localhub.dto.request.RequestRestaurantDto;
import com.localhub.localhub.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "가게 리뷰 작성", description = "유저가 가게 리뷰 작성")
    @PostMapping("/save-review")
    public ResponseEntity<?> saveReview(Authentication authentication,
                                        @RequestBody CreateReview createReview) {
        restaurantService.createReview(authentication.getName(), createReview);

        return null; ///작업중
    }


    @Operation(summary = "가게 정보 수정", description = "ONWER유저가 자신의 가게 수정")
    @PutMapping("/update")
    public ResponseEntity<?> updateRestaurant(Authentication authentication,
                                              @RequestBody RequestRestaurantDto requestRestaurantDto) {


        return null; //작업중
    }
}
