package com.localhub.localhub.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReview {


    private Long restaurantId;
    private String content;
    private Integer score;


}
