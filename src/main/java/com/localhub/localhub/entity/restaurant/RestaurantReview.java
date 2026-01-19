package com.localhub.localhub.entity.restaurant;


import com.localhub.localhub.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "restaurant_review")
@Entity
@Builder
public class RestaurantReview extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "content")
    private String content;

    @Column(name = "user_id")
    private Long userId;
    @Column(name = "restaurant_id")
    private Long restaurantId;

}
