package com.localhub.localhub.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class LocationSearchRequestDto {

    private String divide;
    private BigDecimal lng;      // 경도
    private BigDecimal lat;      // 위도
}
