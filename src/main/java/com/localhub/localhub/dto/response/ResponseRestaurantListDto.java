package com.localhub.localhub.dto.response;

import com.localhub.localhub.entity.restaurant.Category;
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
    private Category category;
    private double score;
    private Long reviewCount;

    private Long favoriteCount;

    private String imageUrl;
    private List<String> keyword;


    public void setKeyword(List<String> keywordList) {
        this.keyword = keywordList;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ResponseRestaurantListDto(
            Long restaurantId,
            String name,
            Category category,
            double score,
            Long reviewCount,
            Long favoriteCount,
            String imageUrl
    ) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.category = category;
        this.score = score;
        this.reviewCount = reviewCount;
        this.favoriteCount = favoriteCount;
        this.imageUrl = imageUrl;
    }

}
