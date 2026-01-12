package com.localhub.localhub.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
@Table(name = "menu")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "restaurant_id")
    private Long restaurantId;
    @Column(name = "price")
    private Integer price;

}
