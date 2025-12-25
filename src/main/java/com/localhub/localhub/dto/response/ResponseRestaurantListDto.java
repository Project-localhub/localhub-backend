package com.localhub.localhub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseRestaurantListDto {

    private Long restaurantId;
    private String name;
    private String category;
    private double score;
    private Integer reviewCount;

    private Integer favoriteCount;

    private String imageUrl;
    private List<String> keyword;



}
