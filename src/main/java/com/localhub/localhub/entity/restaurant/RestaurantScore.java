package com.localhub.localhub.entity.restaurant;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor( access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_score_restaurant")
public class RestaurantScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "score")
    private Integer score;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "restaurant_id")
    private Long restaurantId;


    }
