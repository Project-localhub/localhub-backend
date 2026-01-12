package com.localhub.localhub.service;


import com.localhub.localhub.dto.request.*;
import com.localhub.localhub.dto.response.*;
import com.localhub.localhub.entity.Menu;
import com.localhub.localhub.entity.restaurant.*;
import com.localhub.localhub.geo.repository.PostgisStoreLocationRepository;
import com.localhub.localhub.repository.jdbcReposi.RestaurantScoreRepositoryJDBC;
import com.localhub.localhub.repository.jpaReposi.*;
import com.localhub.localhub.entity.UserEntity;
import com.localhub.localhub.entity.UserType;
import com.localhub.localhub.repository.jdbcReposi.RestaurantRepositoryJDBC;
import com.localhub.localhub.repository.jdbcReposi.RestaurantReviewRepositoryJDBC;
import com.localhub.localhub.repository.jdbcReposi.UserLikeRestaurantRepositoryJDBC;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class RestaurantService {

    private final UserLikeRestaurantRepositoryJPA userLikeRestaurantRepositoryJPA;
    private final RestaurantRepositoryJpa restaurantRepositoryJpa;
    private final RestaurantRepositoryJDBC restaurantRepositoryJDBC;
    private final UserRepository userRepository;
    private final RestaurantReviewRepositoryJDBC restaurantReviewRepositoryJDBC;
    private final UserLikeRestaurantRepositoryJDBC userLikeRestaurantRepositoryJDBC;
    private final RestaurantImageRepositoryJpa restaurantImageRepositoryJpa;
    private final RestaurantKeywordRepositoryJpa restaurantKeywordRepositoryJpa;
    private final ImageUrlResolver imageUrlResolver;
    private final RestaurantScoreRepositoryJDBC restaurantScoreRepositoryJDBC;
    private final PostgisStoreLocationRepository postgisStoreLocationRepository;
    private final MenuRepository menuRepository;


    //가게 등록
    @Transactional
    public void save(String username, RequestRestaurantDto requestRestaurantDto) {

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));

        if (userEntity.getUserType() != UserType.OWNER) {
            throw new IllegalArgumentException("OWNER만 가게등록을 할 수 있습니다.");
        }
        String category = requestRestaurantDto.getCategory();

        //카테고리는 필수값 , enum값이랑 다를때 예외처리
        if (category == null) {
            throw new IllegalArgumentException("카테고리는 null일 수 없습니다.");
        }

        try {
            Category.valueOf(category);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("존재하지 않는 카테고리입니다.");
        }

        Long restaurantId = restaurantRepositoryJDBC.save(userEntity.getId(), requestRestaurantDto);
        if (restaurantId == null) {
            throw new IllegalArgumentException("저장 실패");
        }
        // 이미지 저장
        if (requestRestaurantDto.getImages() != null &&
                !requestRestaurantDto.getImages().isEmpty()) {
            List<RestaurantImages> imagesList = requestRestaurantDto.getImages().stream()
                    .map(dto -> RestaurantImages.builder()
                            .imageKey(dto.getImageKey())
                            .sortOrder(dto.getSortOrder())
                            .restaurantId(restaurantId)
                            .build()
                    ).toList();

            for (RestaurantImages restaurantImages : imagesList) {
                restaurantImageRepositoryJpa.save(restaurantImages);
            }
        }

        // 키워드 저장
        if (requestRestaurantDto.getKeyword() != null &&
                !requestRestaurantDto.getKeyword().isEmpty()) {
            List<RestaurantKeyword> keywordList = requestRestaurantDto.getKeyword().stream()
                    .map(dto -> RestaurantKeyword.builder()
                            .keyword(dto)
                            .restaurantId(restaurantId)
                            .build()
                    ).toList();

            for (RestaurantKeyword restaurantKeyword : keywordList) {
                restaurantKeywordRepositoryJpa.save(restaurantKeyword);
            }
        }
        //가게 위치정보 테이블 저장

        //  PostGIS (JdbcTemplate)
        try {
            postgisStoreLocationRepository.saveLocation(
                    restaurantId,
                    requestRestaurantDto.getLongitude(),
                    requestRestaurantDto.getLatitude()
            );
        } catch (Exception e) {
            log.error("POSTGRES LOCATION SAVE FAIL",e);
            throw new RuntimeException("가게 위치 저장 실패", e);
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

    //가게 이미지 수정
    @Transactional
    public void changeRestaurantImages(String username, Long restaurantId,
                                       List<RequestRestaurantImagesDto> imagesDtos) {

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));


        Restaurant restaurant = restaurantRepositoryJpa.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("레스토랑을 찾을 수 없습니다."));

        if (userEntity.getId() != restaurant.getOwnerId()) {
            throw new IllegalArgumentException("가게 주인만 수정이 가능합니다.");
        }

        restaurantImageRepositoryJpa.deleteByRestaurantId(restaurantId);

        List<RestaurantImages> imagesEntityList = imagesDtos.stream().map(
                dto ->
                        RestaurantImages.builder()
                                .restaurantId(restaurantId)
                                .sortOrder(dto.getSortOrder())
                                .imageKey(dto.getImageKey())
                                .build()
        ).toList();

        for (RestaurantImages image : imagesEntityList) {
            restaurantImageRepositoryJpa.save(image);
        }
    }


    //가게 키워드 수정
    @Transactional
    public void changeRestaurantKeyword(String username, Long restaurantId, List<String> dtoKeywordList) {


        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));


        Restaurant restaurant = restaurantRepositoryJpa.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("가게를 찾을 수 없습니다."));

        if (!restaurant.getOwnerId().equals(userEntity.getId())) {
            throw new IllegalArgumentException("가게 주인만 변경가능합니다.");
        }

        restaurantKeywordRepositoryJpa.deleteByRestaurantId(restaurantId);


        List<RestaurantKeyword> list = dtoKeywordList.stream().map(keywords ->
                RestaurantKeyword.builder()
                        .restaurantId(restaurantId)
                        .keyword(keywords)
                        .build()
        ).toList();

        for (RestaurantKeyword restaurantKeyword : list) {
            restaurantKeywordRepositoryJpa.save(restaurantKeyword);
        }

    }


    //가게 정보 조회
    public ResponseRestaurantDto findRestaurantById(Long restaurantId) {


        Restaurant restaurant = restaurantRepositoryJpa.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("가게 정보를 찾을 수 없습니다."));


        //이미지조회
        List<RestaurantImages> imagesList =
                restaurantImageRepositoryJpa.findByRestaurantId(restaurantId);
        //키워드조회
        List<RestaurantKeyword> keywordList =
                restaurantKeywordRepositoryJpa.findByRestaurantId(restaurantId);

        //이미지키 URL 변환
        List<ResponseRestaurantImageDto> urlList = imagesList.stream().map(
                ent ->

                        ResponseRestaurantImageDto.builder()
                                .sortOrder(ent.getSortOrder())
                                .imageUrl(imageUrlResolver.toPresignedUrl(ent.getImageKey()))
                                .build()
        ).toList();

        //키워드 DTO변환
        List<String> keyList = keywordList.stream().map(ent ->
                ent.getKeyword()
        ).toList();


        //좋아요 갯수
        Integer totalLikeCount = userLikeRestaurantRepositoryJDBC.getTotalLikeCount(restaurantId);

        int totalReviewCount = restaurantReviewRepositoryJDBC.getTotalReviewCount(restaurantId);

        double avg = restaurantScoreRepositoryJDBC.countScore(restaurantId);
        double score = Math.round(avg * 10) / 10.0;
        //찜한 목록 확인
        ResponseRestaurantDto build = ResponseRestaurantDto.builder()
                .id(restaurantId)
                .description(restaurant.getDescription())
                .businessNumber(restaurant.getBusinessNumber())
                .breakEndTime(restaurant.getBreakEndTime())
                .breakStartTime(restaurant.getBreakStartTime())
                .name(restaurant.getName())
                .address(restaurant.getAddress())
                .phone(restaurant.getPhone())
                .keywordList(keyList)
                .imageUrlList(urlList)
                .category(restaurant.getCategory().name())
                .latitude(restaurant.getLatitude())
                .longitude(restaurant.getLongitude())
                .openTime(restaurant.getOpenTime())
                .closeTime(restaurant.getCloseTime())
                .hasBreakTime(restaurant.getHasBreakTime())
                .favoriteCount(totalLikeCount)
                .score(score)
                .reviewCount(totalReviewCount)
                .build();
        return build;
    }


    //가게 삭제
    @Transactional
    public void deleteRestaurant(String username, Long restaurantId) {

        Restaurant restaurant = restaurantRepositoryJDBC.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("가게를 찾을 수 없습니다."));

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

        if (userEntity.getUserType() != UserType.OWNER) {
            throw new IllegalArgumentException("OWNER만 삭제할수있습니다.");
        }


        if (!restaurant.getOwnerId().equals(userEntity.getId())) {
            throw new IllegalArgumentException("가게 주인만 정보를 지울 수 있습니다.");
        }
        //키워드 삭제
        restaurantKeywordRepositoryJpa.deleteByRestaurantId(restaurantId);
        //이미지 삭제
        restaurantImageRepositoryJpa.deleteByRestaurantId(restaurantId);

        int result = restaurantRepositoryJDBC.deleteById(restaurantId);
        if (result == 0) {
            throw new RuntimeException("삭제 실패 서버오류발생");
        }
    }

    //전체 가게목록조회(거리위치정보 동적쿼리 작업중)
    public Page<ResponseRestaurantListDto> getAllRestaurantListWithDistance(LocationSearchRequestDto dto,
                                                                Pageable pageable, String username) {


        //유저 아이디초기화 null이 아닐시에
        Long userId = null;
        if (username != null) {
            userId = userRepository
                    .findByUsername(username)
                    .orElseThrow()
                    .getId();
        }

        Page<ResponseRestaurantListDto> page = restaurantRepositoryJpa.findAllWithScoresWithDistance(pageable,dto.getDivide());
        //뽑아온 레스토랑의 아이디를 뽑아서 list로 만들기 이미지랑 키워드 뽑을때 where in으로 뽑기위함
        List<Long> restaurantIds = page.getContent().stream()
                .map(ResponseRestaurantListDto::getRestaurantId)
                .toList();


        Set<Long> likedRestaurantIds;

        //유저 아이디가 null 아니고 레스토랑 아이디가 empty 아닐때
        //userlikerestuarnat중에 위에 뽑은 restuarnatids의 값과 userid를 가지고 필터링해서
        //좋아요 목록 아이디 get
        if (userId != null && !restaurantIds.isEmpty()) {
            likedRestaurantIds = userLikeRestaurantRepositoryJPA
                    .findRestaurantIdsByUserIdAndRestaurantIdIn(userId, restaurantIds)
                    .stream().map(ulr ->
                            ulr.getRestaurantId()
                    ).collect(Collectors.toSet());
        } else {
            likedRestaurantIds = Set.of();
        }

        //레스토랑 아이디로 해당 키워드 조회(리스트)
        List<RestaurantKeyword> keywords =
                restaurantKeywordRepositoryJpa
                        .findByRestaurantIdIn(restaurantIds);

        //레스토랑 아이디로 그룹핑해서 해당 레스토랑 아이디에 해당하는 키워드들 리스트로 분류 후 MAP으로 뽑기
        Map<Long, List<String>> keywordMap = keywords.stream()
                .collect(Collectors.groupingBy(
                        RestaurantKeyword::getRestaurantId,
                        Collectors.mapping(
                                RestaurantKeyword::getKeyword,
                                Collectors.toList()
                        )
                ));
        //해당 레스토랑 이미지중 1번째 이미지만 뽑아오기
        List<RestaurantImages> firstImageByRestaurantIds =
                restaurantImageRepositoryJpa
                        .findFirstImageByRestaurantIds(restaurantIds);

        Map<Long, String> firstImageMap = firstImageByRestaurantIds.stream().collect(Collectors.toMap(
                RestaurantImages::getRestaurantId,
                RestaurantImages::getImageKey
        ));

        //거리기반 내용 조회
        List<StoreDistanceDto> storeDistancesByIds = postgisStoreLocationRepository.findStoreDistancesByIds
                (restaurantIds, dto.getLng(), dto.getLat(), pageable.getPageSize());

        Map<Long, Double> distanceMap =
                storeDistancesByIds.stream()
                        .collect(Collectors.toMap(
                                StoreDistanceDto::getStoreId,
                                StoreDistanceDto::getDistanceKm
                        ));


        page.stream().forEach(
                pg ->
                {
                    pg.setKeyword(keywordMap.getOrDefault(pg.getRestaurantId(), List.of()));
                    pg.setLiked(likedRestaurantIds.contains(pg.getRestaurantId()));
                    pg.setImageUrl(imageUrlResolver.toPresignedUrl(firstImageMap.get(pg.getRestaurantId())));
                    pg.setDistance(distanceMap.get(pg.getRestaurantId()));
                }
        );
        return page;
    }

    //가게 전체조회(거리정보x)
    public Page<ResponseRestaurantListDto> getAllRestaurantList(Pageable pageable, String username) {


        //유저 아이디초기화 null이 아닐시에
        Long userId = null;
        if (username != null) {
            userId = userRepository
                    .findByUsername(username)
                    .orElseThrow()
                    .getId();
        }

        Page<ResponseRestaurantListDto> page = restaurantRepositoryJpa.findAllWithScores(pageable);
        //뽑아온 레스토랑의 아이디를 뽑아서 list로 만들기 이미지랑 키워드 뽑을때 where in으로 뽑기위함
        List<Long> restaurantIds = page.getContent().stream()
                .map(ResponseRestaurantListDto::getRestaurantId)
                .toList();


        Set<Long> likedRestaurantIds;

        //유저 아이디가 null 아니고 레스토랑 아이디가 empty 아닐때
        //userlikerestuarnat중에 위에 뽑은 restuarnatids의 값과 userid를 가지고 필터링해서
        //좋아요 목록 아이디 get
        if (userId != null && !restaurantIds.isEmpty()) {
            likedRestaurantIds = userLikeRestaurantRepositoryJPA
                    .findRestaurantIdsByUserIdAndRestaurantIdIn(userId, restaurantIds)
                    .stream().map(ulr ->
                            ulr.getRestaurantId()
                    ).collect(Collectors.toSet());
        } else {
            likedRestaurantIds = Set.of();
        }

        //레스토랑 아이디로 해당 키워드 조회(리스트)
        List<RestaurantKeyword> keywords =
                restaurantKeywordRepositoryJpa
                        .findByRestaurantIdIn(restaurantIds);

        //레스토랑 아이디로 그룹핑해서 해당 레스토랑 아이디에 해당하는 키워드들 리스트로 분류 후 MAP으로 뽑기
        Map<Long, List<String>> keywordMap = keywords.stream()
                .collect(Collectors.groupingBy(
                        RestaurantKeyword::getRestaurantId,
                        Collectors.mapping(
                                RestaurantKeyword::getKeyword,
                                Collectors.toList()
                        )
                ));
        //해당 레스토랑 이미지중 1번째 이미지만 뽑아오기
        List<RestaurantImages> firstImageByRestaurantIds =
                restaurantImageRepositoryJpa
                        .findFirstImageByRestaurantIds(restaurantIds);

        Map<Long, String> firstImageMap = firstImageByRestaurantIds.stream().collect(Collectors.toMap(
                RestaurantImages::getRestaurantId,
                RestaurantImages::getImageKey
        ));


        page.stream().forEach(
                pg ->
                {
                    pg.setKeyword(keywordMap.getOrDefault(pg.getRestaurantId(), List.of()));
                    pg.setLiked(likedRestaurantIds.contains(pg.getRestaurantId()));
                    pg.setImageUrl(imageUrlResolver.toPresignedUrl(firstImageMap.get(pg.getRestaurantId())));
                }
        );
        return page;
    }



    //리뷰작성
    @Transactional

    public void createReview(String username, CreateReview createReview) {

        Long savedScoreId;

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));


        Restaurant restaurant = restaurantRepositoryJDBC.findById(createReview.getRestaurantId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 가게입니다."));

        if (restaurant.getOwnerId().equals(userEntity.getId())) {
            throw new IllegalArgumentException("자신의 가게에는 리뷰를 작성할 수 없습니다.");
        }
        Integer score = createReview.getScore();
        if (score != null) {
            if (score < 1 || score > 5) {
                throw new IllegalArgumentException("별점은 1~5 사이여야 합니다.");
            }
            //지금은 한 유저가 한 가게에 1개의 리뷰밖에 못달음(score 유니크조건때문에 나중에 리팩토링필요할수도)
            savedScoreId = restaurantScoreRepositoryJDBC.save
                    (userEntity.getId(), restaurant.getId(), createReview.getScore());
        }

        int save = restaurantReviewRepositoryJDBC.save(userEntity.getId(), createReview);
        if (save == 0) {
            throw new RuntimeException("db 저장 실패");
        }
    }

    //가게 찜하기
    @Transactional
    public void likeRestaurant(String username, Long restaurantId) {

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

        Restaurant restaurant = restaurantRepositoryJpa.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지않는 가게입니다."));

        int isExist = userLikeRestaurantRepositoryJDBC.isExistByUserIdAndRestaurantId
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

    //찜한 목록 조회
    public Page<ResponseRestaurantListDto> getLikeList(Pageable pageable, String username) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

        if (!userLikeRestaurantRepositoryJPA.isExistByUserId(userEntity.getId())) {
            return Page.empty();
        }

        Page<ResponseRestaurantListDto> page = userLikeRestaurantRepositoryJPA.findLikedRestaurant(userEntity.getId(), pageable);

        List<RestaurantKeyword> all = restaurantKeywordRepositoryJpa.findAll();

        Map<Long, List<String>> keywordMap = all.stream().collect(Collectors.groupingBy(
                keyword -> keyword.getRestaurantId(),
                Collectors.mapping(keyword -> keyword.getKeyword(),
                        Collectors.toList())
        ));

        page.stream().forEach(
                pg ->
                {

                    pg.setKeyword(keywordMap.getOrDefault(pg.getRestaurantId(), List.of()));
                    pg.setImageUrl(imageUrlResolver.toPresignedUrl(pg.getImageUrl()));

                }
        );
        return page;
    }

    //OWNER가 자신의 가게 조회
    public List<ResponseRestaurantDto> findByOwner(String username, Pageable pageable) {

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다."));

        if (userEntity.getUserType() != UserType.OWNER) {
            throw new IllegalArgumentException("OWNER 유저가 아닙니다.");
        }

        //레스토랑 조회
        List<ResponseRestaurantDto> restaurantListDtoPage =
                restaurantRepositoryJDBC.findAllWithScoresByOwner(userEntity.getId());

        //레스토랑 아이디 추출
        List<Long> restaurantIdList = restaurantListDtoPage.stream().map(
                ResponseRestaurantDto::getId
        ).toList();


        //키워드 조회

        List<RestaurantKeyword> restaurantKeywordList =
                restaurantKeywordRepositoryJpa.findByRestaurantIdIn(restaurantIdList);

        //RestaurantId의 키 값을 기준으로 RestaurantKeyword의 getKeyword로 키워드 값을 뽑아내서 키,밸류 맵 변환

        Map<Long, List<String>> keywordMap = restaurantKeywordList.stream().collect(Collectors.groupingBy(
                RestaurantKeyword::getRestaurantId,
                Collectors.mapping(
                        RestaurantKeyword::getKeyword,
                        Collectors.toList()
                )
        ));


        //이미지 조회
        List<RestaurantImages> restaurantImagesList =
                restaurantImageRepositoryJpa.findByREstaurantIdIns(restaurantIdList);


        //이미지키 URL 변환 및 RestaurantId에 키값을 기준으로 ResponseRestaurantImageDto 키,밸류 맵 변환

        Map<Long, List<ResponseRestaurantImageDto>> imageMap = restaurantImagesList.stream().collect(Collectors.groupingBy(
                RestaurantImages::getRestaurantId,
                Collectors.mapping(
                        (ent) ->
                                ResponseRestaurantImageDto.builder()
                                        .imageUrl(imageUrlResolver.toPresignedUrl(ent.getImageKey()))
                                        .sortOrder(ent.getSortOrder())
                                        .build(),
                        Collectors.toList()
                )
        ));


        restaurantListDtoPage.stream().forEach(
            pg->
                {
                    pg.setImageUrlList(imageMap.getOrDefault(pg.getId(), List.of()));
                    pg.setKeywordList(keywordMap.getOrDefault(pg.getId(), List.of()));
                }
        );

        return restaurantListDtoPage;
}

    public Page<ResponseReviewDto> getReviewByRestaurantId(Long restaurantId, Pageable pageable) {

        long offset = pageable.getOffset();
        int size = pageable.getPageSize();

       List<ResponseReviewDto> reviewDtoList =
               restaurantReviewRepositoryJDBC.findByRestaurantId(restaurantId,offset,size);

        Long totalCount = restaurantReviewRepositoryJDBC.countByRestaurantId(restaurantId);

        return new PageImpl<>(reviewDtoList,pageable,totalCount);


    }

    //찜한 목록 삭제
    @Transactional
    public void deleteLikeRestaurant(Long restaurantId, String username) {

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));


        int existByUserIdAndRestaurantId = userLikeRestaurantRepositoryJDBC
                .isExistByUserIdAndRestaurantId(userEntity.getId(), restaurantId);

        if (existByUserIdAndRestaurantId == 0) {
            throw new IllegalArgumentException("찜한 목록이 없습니다.");
        }

        int result = userLikeRestaurantRepositoryJDBC
                .deleteByUserIdAndRestaurantId(userEntity.getId(), restaurantId);
        if (result == 0) {
            throw new IllegalArgumentException("삭제할 찜할 목록이 없습니다.");
        }
    }

    public void addMenu(String username,List<CreateMenu> dtoList) {

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저"));

        List<Restaurant> restaurantList =
                restaurantRepositoryJpa.findByOwnerId(userEntity.getId());

        Set<Long> restaurantIds = restaurantList.stream().map(restaurant ->
                restaurant.getId()
        ).collect(Collectors.toSet());

        boolean invalid = dtoList.stream().anyMatch(id -> !restaurantIds.contains(id));

        if (invalid) {
            throw new IllegalArgumentException("점주만 작업가능");
        }
        List<Menu> list = dtoList.stream().map(dto ->
                Menu.builder()
                        .restaurantId(dto.getRestaurantId())
                        .price(dto.getPrice())
                        .name(dto.getName())
                        .build()
        ).toList();
        menuRepository.saveAll(list);

    }

}
