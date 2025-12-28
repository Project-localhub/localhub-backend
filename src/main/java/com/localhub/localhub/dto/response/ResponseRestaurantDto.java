package com.localhub.localhub.dto.response;


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
    private LocalTime openTime;
    private LocalTime closeTime;
    private Boolean hasBreakTime;
    private LocalTime breakStartTime;
    private LocalTime breakEndTime;
    private Integer reviewCount;
    private Integer favoriteCount;
    private double score;

    private List<String> keywordList;
    private List<ResponseRestaurantImageDto> imageUrlList;
}
