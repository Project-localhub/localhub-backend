package com.localhub.localhub.dto.response;

import com.localhub.localhub.entity.restaurant.Category;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    private double latitude;
    private double longitude;

    private boolean liked;

    private double distance;



    public void setKeyword(List<String> keywordList) {
        this.keyword = keywordList;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }


    public ResponseRestaurantListDto(
            Long restaurantId,
            String name,
            Category category,
            double score,
            Long reviewCount,
            Long favoriteCount,
            String imageUrl,
            double latitude,
            double longitude
    ) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.category = category;
        this.score = score;
        this.reviewCount = reviewCount;
        this.favoriteCount = favoriteCount;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
