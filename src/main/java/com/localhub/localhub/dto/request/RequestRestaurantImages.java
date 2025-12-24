package com.localhub.localhub.dto.request;

import jakarta.annotation.security.DenyAll;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RequestRestaurantImages {

    private String imageKey;
    private Integer sortOrder;
}
