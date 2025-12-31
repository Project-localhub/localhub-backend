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


    @Query(value = """
            SELECT 
            new com.localhub.localhub.dto.response.ResponseRestaurantDto
            (
            r.id,
            r.name,
            r.businessNumber,
            r.description,
            r.category,
            r.phone,
            r.address,
            r.latitude,
            r.longitude,
            r.openTime,
            r.closeTime,
            r.hasBreakTime,
            r.breakStartTime,
            r.breakEndTime,
            COUNT(DISTINCT rv.id),
            COUNT(DISTINCT uls.id),
            COALESCE(AVG(rs.score),0) AS score
            )

            FROM Restaurant r

            LEFT JOIN RestaurantScore rs
            ON rs.restaurantId = r.id
            LEFT JOIN RestaurantReview rv
            ON rv.restaurantId = r.id
            LEFT JOIN UserLikeRestaurant uls
            on uls.restaurantId = r.id

            WHERE r.ownerId = :ownerId

            GROUP BY
             r.id,
            r.name,
            r.businessNumber,
            r.description,
            r.category,
            r.phone,
            r.address,
            r.latitude,
            r.longitude,
            r.openTime,
            r.closeTime,
            r.hasBreakTime,
            r.breakStartTime,
            r.breakEndTime
            """
    )
    Page<ResponseRestaurantDto> findAllWithScoresByOwner(@Param("ownerId") Long ownerId, Pageable pageable);



    @Query("""
            SELECT r
            FROM Restaurant r
            WHERE r.ownerId = :ownerId
            """)
    List<Restaurant> findByOwnerId(@Param("ownerId") Long id);
}
