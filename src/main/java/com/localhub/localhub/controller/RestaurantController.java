package com.localhub.localhub.controller;

import com.localhub.localhub.dto.request.*;
import com.localhub.localhub.dto.response.ResponseMenu;
import com.localhub.localhub.dto.response.ResponseRestaurantDto;
import com.localhub.localhub.dto.response.ResponseRestaurantListDto;
import com.localhub.localhub.dto.response.ResponseReviewDto;
import com.localhub.localhub.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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

        log.info("리뷰 컨트롤러 호출");
        restaurantService.createReview(authentication.getName(), createReview);
        return ResponseEntity.ok("'가게 리뷰 작성 완료");
    }

    @Operation(summary = "가게 정보 수정", description = "ONWER유저가 자신의 가게 수정")
    @PutMapping("/update")
    public ResponseEntity<?> updateRestaurant(Authentication authentication,
                                              @RequestBody RequestRestaurantDto requestRestaurantDto) {

        restaurantService.updateRestaurantInfo(authentication.getName(), requestRestaurantDto);
        return ResponseEntity.ok("가게정보 수정 완료");
    }

    @Operation(summary = "가게 이미지 변경", description = "가게OWNER 유저가 자신의 가게 이미지변경")
    @PostMapping("/updateImages/{restaurantId}")
    public ResponseEntity<?> changeRestaurantImages(Authentication authentication,
                                                    @RequestBody List<RequestRestaurantImagesDto> dtoList,
                                                    @PathVariable("restaurantId") Long restaurantId
    ) {
        restaurantService.changeRestaurantImages(authentication.getName(), restaurantId, dtoList);
        return ResponseEntity.ok("가게 이미지 변경 완료.");

    }

    @Operation(summary = "가게 키워드 변경", description = "가게OWNER 유저가 자신의 가게 키워드 변경")
    @PostMapping("/updateKeywords/{restaurantId}")
    public ResponseEntity<?> changeRestaurantKeyword(Authentication authentication,
                                                     @RequestBody List<String> keywords,
                                                     @PathVariable("restaurantId") Long restaurantId) {
        restaurantService.changeRestaurantKeyword(authentication.getName(), restaurantId, keywords);
        return ResponseEntity.ok("가게 키워드 변경 완료");

    }

    @Operation(summary = "가게 상세정보 조회",
            description = "가게의 아이디를 param으로 받고 해당 가게 상세정보 조회")
    @GetMapping("/{restaurantId}")
    public ResponseEntity<ResponseRestaurantDto> getRestaurantInfoById
            (@PathVariable("restaurantId") Long restaurantId) {


        ResponseRestaurantDto result = restaurantService.findRestaurantById(restaurantId);
        return ResponseEntity.ok(result);

    }

    @Operation(summary = "가게삭제", description = "OWNER유저가 등록 가게 삭제")
    @DeleteMapping("/delete/{restaurantId}")
    public ResponseEntity<?> deleteRestaurant(Authentication authentication,
                                              @PathVariable("restaurantId") Long restaurantId) {


        restaurantService.deleteRestaurant(authentication.getName(), restaurantId);
        return ResponseEntity.ok("가게 삭제 완료");

    }

    @Operation(summary = "가게 전체 목록 조회", description = """
            가게 전체 목록 조회(페이징처리)
            디폴트 값 사이즈 10
            """)
    @GetMapping("/get-all-restaurants")
    public ResponseEntity<Page<ResponseRestaurantListDto>> getAllRestaurants(
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            Authentication authentication) {


        String username = authentication != null
                ? authentication.getName()
                : null;

        Page<ResponseRestaurantListDto> result = restaurantService.getAllRestaurantList(pageable, username);
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "가게 전체 목록 필터 기반 조회", description = """
            가게 전체 목록 조회(페이징처리)
            디폴트 값 사이즈 10
            """)
    @GetMapping("/get-all-restaurantsByFilter")
    public ResponseEntity<Page<ResponseRestaurantListDto>> getAllRestaurantsByFilter(
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            Authentication authentication,
            @ModelAttribute RequestRestaurantFilter dto) {


        String username = authentication != null
                ? authentication.getName()
                : null;

        Page<ResponseRestaurantListDto> result = restaurantService.getAllRestaurantListWithDistance
                (dto,pageable, username);
        return ResponseEntity.ok(result);
    }



    @Operation(summary = "찜하기", description = "유저가 마음에 드는 가게 찜하기 기능")
    @PostMapping("/like/{restaurantId}")
    public ResponseEntity<?> likeRestaurant(Authentication authentication,
                                            @PathVariable("restaurantId") Long restaurantId) {


        restaurantService.likeRestaurant(authentication.getName(), restaurantId);
        return ResponseEntity.ok("찜 완료");

    }

    @Operation(summary = "찜목록 조회", description = "유저의 찜목록 조회")
    @GetMapping("/get/likeList")
    public ResponseEntity<Page<ResponseRestaurantListDto>> getLikeList(
            Authentication authentication,
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        Page<ResponseRestaurantListDto> result = restaurantService.getLikeList(pageable, authentication.getName());
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "OWNER가 자신의 가게조회", description = """
            OWNER유저가 자신의 가게조회,OWNER가 아니면 400 에러 발생,
            가게 정책은 한 OWNER가 하나의 가게만 등록가능
            """)
    @GetMapping("/findByOwnerId")
    public ResponseEntity<List<ResponseRestaurantDto>> findByOwnerID
            (Authentication authentication,
             @ParameterObject
             @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
             Pageable pageable) {

        return ResponseEntity.ok(restaurantService.findByOwner(authentication.getName(), pageable));
    }

    @Operation(summary = "찜목록 삭제", description = """
            
            유저가 식당의 id를 파라미터로받고 찜한 상태라면
            해당 id의 식당을 찜한 목록에서 삭제
            """)
    @DeleteMapping("/deleteBy/{restaurantId}")
    public ResponseEntity<?> deleteByRestaurantId(Authentication authentication,
                                                  @PathVariable Long restaurantId) {

        restaurantService.deleteLikeRestaurant(restaurantId, authentication.getName());
        return ResponseEntity.ok("찜목록 삭제 완료 해당 식당 id : " + restaurantId);
    }

    @Operation(summary = "식당리뷰조회", description = """
            
            식당의 id를 기준으로 해당 식당의 리뷰 전체 페이징조회
            """)
    @GetMapping("/getReviewBy/{restaurantId}")
    public ResponseEntity<Page<ResponseReviewDto>> getReviewByRestaurantId
            (@PathVariable("restaurantId") Long restaurantId,
             @ParameterObject
             @PageableDefault(size = 10) Pageable pageable
            ) {

        Page<ResponseReviewDto> result = restaurantService.getReviewByRestaurantId(restaurantId, pageable);
        return ResponseEntity.ok(result);

    }

    @Operation(summary = "메뉴추가")
    @PostMapping("/addMenu")
    public ResponseEntity<String> addMenu(@RequestBody List<CreateMenu> createMenu,
                                          Authentication authentication) {

        restaurantService.addMenu(authentication.getName(), createMenu);
        return ResponseEntity.ok("메뉴등록완료");

    }

    @Operation(summary = "메뉴조회")
    @GetMapping("/getMenu/{restaurantId}")
    public ResponseEntity<List<ResponseMenu>> getMenu(@PathVariable("restaurantId") Long restaurantId) {

        List<ResponseMenu> menus = restaurantService.getMenus(restaurantId);
        return ResponseEntity.ok(menus);

    }

    @Operation(summary = "메뉴수정(삭제포함)")
    @PutMapping("/updateMenu")
    public ResponseEntity<String> updateMenu(@RequestBody List<CreateMenu> createMenu,
                                             Authentication authentication) {

        restaurantService.updateMenu(authentication.getName(), createMenu);
        return ResponseEntity.ok("메뉴수정완료");
    }
}
