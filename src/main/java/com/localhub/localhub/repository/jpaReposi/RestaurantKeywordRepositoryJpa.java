package com.localhub.localhub.repository.jpaReposi;

import com.localhub.localhub.entity.restaurant.RestaurantKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Modifying;
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


    @Query ("""
            DELETE 
            FROM RestaurantKeyword key
            WHERE key.restaurantId = :restaurantId
            """)
    @Modifying
    void deleteByRestaurantId(@Param("restaurantId") Long restaurantId);


    @Query("""
    select rk
    from RestaurantKeyword rk
    where rk.restaurantId in :restaurantIds
        """)
    List<RestaurantKeyword> findByRestaurantIdIn(List<Long> restaurantIds);

}
