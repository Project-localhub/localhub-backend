package com.localhub.localhub.repository.jpaReposi;

import com.localhub.localhub.dto.response.ResponseRestaurantDto;
import com.localhub.localhub.dto.response.ResponseRestaurantListDto;
import com.localhub.localhub.entity.restaurant.UserLikeRestaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface UserLikeRestaurantRepositoryJPA extends JpaRepository<UserLikeRestaurant, Long> {

    @Query(
            value = """
                    SELECT
                        r.id                  AS id,
                        r.name                AS name,
                        r.business_number     AS businessNumber,
                        r.description         AS description,
                        r.category            AS category,
                        r.phone               AS phone,
                        r.address             AS address,
                        r.latitude            AS latitude,
                        r.longitude           AS longitude,
                        r.open_time           AS openTime,
                        r.close_time          AS closeTime,
                        r.has_break_time      AS hasBreakTime,
                        r.break_start_time    AS breakStartTime,
                        r.break_end_time      AS breakEndTime
                    
                    
                    FROM user_like_restaurant ulr
                    JOIN restaurant r
                      ON r.id = ulr.restaurant_id
                    WHERE ulr.user_id = :userId
                    LIMIT :limit OFFSET :offset
                    """,
            nativeQuery = true
    )
    List<ResponseRestaurantDto> findRestaurantByOwner(
            @Param("userId") Long userId,
            @Param("limit") int limit,
            @Param("offset") int offset);



    @Query("""
            
            SELECT new com.localhub.localhub.dto.response.ResponseRestaurantListDto
            (
            r.id,
            r.name,
            r.category,
            COALESCE(AVG(rs.score),0),
            COUNT(DISTINCT rv.id),
            COUNT(DISTINCT ulr.id),
            rim.imageKey
            )
            
            FROM Restaurant r
            
               
            LEFT JOIN UserLikeRestaurant url
            on url.restaurantId = r.id
            
            LEFT JOIN RestaurantReview rv
            on rv.restaurantId = r.id
            
            LEFT JOIN RestaurantScore rs
            on rs.restaurantId = r.id
            
            LEFT JOIN UserLikeRestaurant ulr
            on ulr.restaurantId = r.id
             
            LEFT JOIN RestaurantImages rim
            on rim.restaurantId = r.id AND rim.sortOrder = 1
            
            WHERE url.userId = :userId 
            
            GROUP BY
            r.id, r.name, r.category, rim.imageKey
            
            
            
            """)
    Page<ResponseRestaurantListDto> findLikedRestaurant(@Param("userId") Long userId, Pageable pageable);


    @Query("""
            SELECT CASE WHEN
            COUNT(url) > 0 THEN TRUE 
            ELSE FALSE 
            END
            FROM UserLikeRestaurant url
            WHERE url.userId = :userId
            
            """)
    boolean isExistByUserId(@Param("userId") Long userId);


    @Query("""
            
            SELECT ul
            FROM UserLikeRestaurant ul
            WHERE ul.userId = :userId
            AND ul.restaurantId in :restaurantIds
            
            """)
   List<UserLikeRestaurant> findRestaurantIdsByUserIdAndRestaurantIdIn(@Param("userId") Long userId,
                                                                       @Param("restaurantIds") List<Long> restaurantIds);
}



