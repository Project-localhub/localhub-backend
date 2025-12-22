package com.localhub.localhub.entity.restaurant;

import com.localhub.localhub.dto.request.RequestRestaurantDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Entity
@Builder
@Table(name = "restaurant")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id")
    private Long ownerId;

    private String name;

    @Column(name = "business_number", length = 12)
    private String businessNumber;

    @Lob
    private String description;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String phone;

    private String address;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;
    @Column(name ="open_time")
    private LocalTime openTime;
    @Column(name = "close_time")
    private LocalTime closeTime;

    @Column(name = "has_break_time", nullable = true)
    private Boolean hasBreakTime;

    @Column(name = "break_start_time")
    private LocalTime breakStartTime;

    @Column(name = "break_end_time")
    private LocalTime breakEndTime;


    public void update(RequestRestaurantDto dto) {

        if (dto.getName() != null) {
            this.name = dto.getName();
        }

        if (dto.getBusinessNumber() != null) {
            this.businessNumber = dto.getBusinessNumber();
        }

        if (dto.getDescription() != null) {
            this.description = dto.getDescription();
        }

        if (dto.getCategory() != null) {
            this.category = Category.valueOf(dto.getCategory());
        }

        if (dto.getPhone() != null) {
            this.phone = dto.getPhone();
        }

        if (dto.getAddress() != null) {
            this.address = dto.getAddress();
        }

        if (dto.getLatitude() != null) {
            this.latitude = dto.getLatitude();
        }

        if (dto.getLongitude() != null) {
            this.longitude = dto.getLongitude();
        }

        if (dto.getOpenTime() != null) {
            this.openTime = dto.getOpenTime();
        }

        if (dto.getCloseTime() != null) {
            this.closeTime = dto.getCloseTime();
        }

        if (dto.getHasBreakTime() != null) {
            this.hasBreakTime = dto.getHasBreakTime();
        }

        if (dto.getBreakStartTime() != null) {
            this.breakStartTime = dto.getBreakStartTime();
        }

        if (dto.getBreakEndTime() != null) {
            this.breakEndTime = dto.getBreakEndTime();
        }
    }
}
