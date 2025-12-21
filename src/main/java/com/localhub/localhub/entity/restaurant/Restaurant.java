package com.localhub.localhub.entity.restaurant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Entity
@Table(name = "restaurant")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    private LocalTime openTime;

    private LocalTime closeTime;

    @Column(name = "has_break_time", nullable = false)
    private boolean hasBreakTime;

    @Column(name = "break_start_time")
    private LocalTime breakStartTime;

    @Column(name = "image_key")
    private String imageKey;


}
