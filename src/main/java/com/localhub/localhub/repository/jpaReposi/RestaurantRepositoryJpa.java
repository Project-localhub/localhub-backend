package com.localhub.localhub.repository.jpaReposi;

import com.localhub.localhub.entity.restaurant.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RestaurantRepositoryJpa extends JpaRepository<Restaurant, Long> {


    @Query("""
            SELECT r
            (
            r.id,
            r.name,
            r.category,
            COALESCE(AVG(rs.score),0)
            )
            FROM Restaurant r
            LEFT JOIN RestaurantScore rs
            ON rs.restaurantId = r.id
            GROUP BY r.id, r.name
            
            """)
    Page<Restaurant> findAllWithScores(Pageable pageable);

}
