package com.localhub.localhub.repository.jpaReposi;

import com.localhub.localhub.dto.response.ResponseRestaurantDto;
import com.localhub.localhub.entity.restaurant.UserLikeRestaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
                        r.break_end_time      AS breakEndTime,
                    
                    
                    FROM user_like_restaurant ulr
                    JOIN restaurant r
                      ON r.id = ulr.restaurant_id
                    WHERE ulr.user_id = :userId
                    LIMIT :limit OFFSET :offset
                    """,
            nativeQuery = true
    )
    List<ResponseRestaurantDto> findLikedRestaurants(
            @Param("userId") Long userId,
            @Param("limit") int limit,
            @Param("offset") int offset);

}



