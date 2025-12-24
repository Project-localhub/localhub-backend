package com.localhub.localhub.repository.jpaReposi;

import com.localhub.localhub.entity.restaurant.RestaurantImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantImageRepositoryJpa extends JpaRepository<RestaurantImages,Long> {

  @Query("""
          SELECT img
          FROM RestaurantImages img
          WHERE img.restaurantId = :restaurantId
          
          """)
    List<RestaurantImages> findByRestaurantId(@Param("restaurantId") Long restaurantId);

}
