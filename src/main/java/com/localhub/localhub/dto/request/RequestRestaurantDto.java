package com.localhub.localhub.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class RequestRestaurantDto {
    @Schema(description = "가게 ID (수정 시에만 사용)", example = "수정시에만,생성시에는 값비워둘것")
    private Long id;

    @Schema(description = "가게 이름", example = "홍콩반점")
    private String name;

    @Schema(description = "사업자 번호", example = "123456789012")
    private String businessNumber;

    @Schema(description = "가게 설명", example = "짬뽕이 맛있는 집")
    private String description;

    @Schema(description = "가게 카테고리", example = "한식")
    private String category;

    @Schema(description = "가게 전화번호", example = "02-123-4567")
    private String phone;

    @Schema(description = "가게 주소", example = "서울시 강남구")
    private String address;

    @Schema(description = "위도", example = "37.1234567")
    private BigDecimal latitude;

    @Schema(description = "경도", example = "127.1234567")
    private BigDecimal longitude;

    // ================== 시간 필드  ==================

    @Schema(
            description = "영업 시작 시간 (HH:mm)",
            example = "09:00",
            type = "string",
            format = "time"
    )
    @JsonFormat(pattern = "HH:mm")
    private LocalTime openTime;

    @Schema(
            description = "영업 종료 시간 (HH:mm)",
            example = "22:00",
            type = "string",
            format = "time"
    )
    @JsonFormat(pattern = "HH:mm")
    private LocalTime closeTime;

    @Schema(description = "브레이크 타임 여부", example = "true")
    private Boolean hasBreakTime;

    @Schema(
            description = "브레이크 시작 시간 (HH:mm)",
            example = "15:00",
            type = "string",
            format = "time"
    )
    @JsonFormat(pattern = "HH:mm")
    private LocalTime breakStartTime;

    @Schema(
            description = "브레이크 종료 시간 (HH:mm)",
            example = "16:00",
            type = "string",
            format = "time"
    )
    @JsonFormat(pattern = "HH:mm")
    private LocalTime breakEndTime;

    // ================== 기타 ==================

    @Schema(description = "가게 키워드 목록", example = "[\"맛집\", \"혼밥\"]")
    private List<String> keyword;

    @Schema(description = "가게 이미지 목록")
    private List<RequestRestaurantImages> images;
}


