package com.localhub.localhub.entity;

import com.localhub.localhub.entity.restaurant.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepositoryJpa extends JpaRepository<Restaurant, Long> {



}
