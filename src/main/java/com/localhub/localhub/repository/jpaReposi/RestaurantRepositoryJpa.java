package com.localhub.localhub.repository.jpaReposi;

import com.localhub.localhub.dto.response.ResponseRestaurantDto;
import com.localhub.localhub.dto.response.ResponseRestaurantListDto;
import com.localhub.localhub.entity.restaurant.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepositoryJpa extends JpaRepository<Restaurant, Long> {


    @Query(value = """
            SELECT new com.localhub.localhub.dto.response.ResponseRestaurantListDto
            (
            r.id,
            r.name,
            r.category,
            COALESCE(AVG(rs.score),0),
            COUNT(DISTINCT rv.id),
            COUNT(DISTINCT uls.id),
            rim.imageKey
            )
            FROM Restaurant r
            LEFT JOIN RestaurantScore rs
            ON rs.restaurantId = r.id
            LEFT JOIN RestaurantReview rv
            ON rv.restaurantId = r.id
            LEFT JOIN UserLikeRestaurant uls 
            on uls.restaurantId = r.id
           
            LEFT JOIN RestaurantImages rim
            on rim.restaurantId = r.id
             AND rim.sortOrder = 1
            
            GROUP BY r.id, r.name , r.category , rim.imageKey
            """
    )
    Page<ResponseRestaurantListDto> findAllWithScores(Pageable pageable);





    Optional<Restaurant> findByOwnerId(Long id);
}
