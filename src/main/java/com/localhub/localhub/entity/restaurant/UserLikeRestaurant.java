package com.localhub.localhub.entity.restaurant;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "user_like_restaurant",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_restaurant",
                columnNames = {"user_id", "restaurant_id"}

        ))
@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserLikeRestaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "restaurant_id")
    private Long restaurantId;

}
