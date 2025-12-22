package com.localhub.localhub.entity.restaurant;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "restaurant_images")
public class RestaurantImages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "image_key")
    private String imageKey;
    @Column(name = "restaurant_id ")
    private Long restaurantId;

}
