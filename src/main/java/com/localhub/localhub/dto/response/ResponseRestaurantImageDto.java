package com.localhub.localhub.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseRestaurantImageDto {

    private String imageUrl;
    private Integer sortOrder;

}
