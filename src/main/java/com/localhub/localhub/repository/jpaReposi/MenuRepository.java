package com.localhub.localhub.repository.jpaReposi;

import com.localhub.localhub.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu,Long> {

    @Query("""
            
            SELECT m
            FROM Menu m
            WHERE m.restaurantId = :restaurantId
            """)
    List<Menu> findByRestaurnatId(@Param("restaurantId") Long restaurantId);
}
