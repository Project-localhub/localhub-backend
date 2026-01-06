package com.localhub.localhub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreDistanceDto {
    private Long storeId;
    private Double distanceKm;
}
