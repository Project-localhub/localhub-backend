package com.localhub.localhub.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.localhub.localhub.entity.restaurant.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseRestaurantDto {


    private Long id;
    private String name;
    private String businessNumber;
    private String description;
    private String category;
    private String phone;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime openTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime closeTime;
    private Boolean hasBreakTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime breakStartTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime breakEndTime;
    private Integer reviewCount;
    private Integer favoriteCount;
    private double score;

    private List<String> keywordList;
    private List<ResponseRestaurantImageDto> imageUrlList;


    public void setImageUrlList(List<ResponseRestaurantImageDto> setImageUrlList) {

        this.imageUrlList = setImageUrlList;
    }


    public void setKeywordList(List<String> setKeywordList) {

        this.keywordList = setKeywordList;
    }


    public ResponseRestaurantDto(
            Long id,
            String name,
            String businessNumber,
            String description,
            Category category,        //  enum으로 받는다
            String phone,
            String address,
            BigDecimal latitude,
            BigDecimal longitude,
            LocalTime openTime,
            LocalTime closeTime,
            Boolean hasBreakTime,
            LocalTime breakStartTime,
            LocalTime breakEndTime,
            Long reviewCount,
            Long favoriteCount,
            Double score
    ) {
        this.id = id;
        this.name = name;
        this.businessNumber = businessNumber;
        this.description = description;

        this.category = category == null ? null : category.name();

        this.phone = phone;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.hasBreakTime = hasBreakTime;
        this.breakStartTime = breakStartTime;
        this.breakEndTime = breakEndTime;

        // COUNT / AVG → Integer 유지
        this.reviewCount = reviewCount == null ? 0 : reviewCount.intValue();
        this.favoriteCount = favoriteCount == null ? 0 : favoriteCount.intValue();
        this.score = score == null ? 0.0 : score;
    }

}
