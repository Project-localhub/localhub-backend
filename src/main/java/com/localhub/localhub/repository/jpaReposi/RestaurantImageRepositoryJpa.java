package com.localhub.localhub.repository.jpaReposi;

import com.localhub.localhub.entity.restaurant.RestaurantImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

  @Modifying
    @Query("""
            DELETE 
            FROM RestaurantImages rs
            WHERE rs.restaurantId = :restaurantId
            """)
    void deleteByRestaurantId(@Param("restaurantId") Long restaurantId);

  @Query("""
            SELECT ri
            FROM RestaurantImages ri
            WHERE ri.restaurantId in :restaurantIds
            and ri.sortOrder = 1
          """)
  List<RestaurantImages> findFirstImageByRestaurantIds
          (@Param("restaurantIds") List<Long> restaurantIds);


  @Query("""
          SELECT ri
          FROM RestaurantImages ri
          WHERE ri.restaurantId in :restaurantIds
          """)
    List<RestaurantImages> findByREstaurantIdIns(@Param("restaurantIds")List<Long> restaurantIdList);


}
