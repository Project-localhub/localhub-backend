package com.localhub.localhub.repository.jpaReposi;

import com.localhub.localhub.entity.restaurant.RestaurantScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantScoreRepositoryJpa extends JpaRepository<RestaurantScore,Long> {
}
