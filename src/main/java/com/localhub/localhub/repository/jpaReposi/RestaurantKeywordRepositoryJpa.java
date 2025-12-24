package com.localhub.localhub.repository.jpaReposi;

import com.localhub.localhub.entity.restaurant.RestaurantKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantKeywordRepositoryJpa  extends JpaRepository<RestaurantKeyword,Long> {

    @Query("""
            SELECT key
            FROM RestaurantKeyword key
            WHERE key.restaurantId = :restaurantId
            """)
    List<RestaurantKeyword> findByRestaurantId(@Param("restaurantId") Long restaurantId);

}
