package com.localhub.localhub.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class RequestRestaurantDto {
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


    private List<String> keyword;
    private List<RequestRestaurantImages> images;


}
