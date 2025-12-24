package com.localhub.localhub.repository.jdbcReposi;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor
public class UserLikeRestaurantRepositoryJDBC {

    private final NamedParameterJdbcTemplate template;


    public int save(Long userId, Long restaurantId) {

        String sql = """
                INSERT 
                INTO user_like_restaurant
                (user_id, restaurant_id)
                VALUES (:user_id, :restaurant_id)
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);
        params.addValue("restaurant_id", restaurantId);


        return template.update(sql, params);

    }

    public int isExistByUserIdAndRestaurantId(Long userId, Long restaurantId) {
//        String sql = """
//        SELECT EXISTS (
//            SELECT 1
//            FROM user_like_restaurant
//            WHERE user_id = :userId
//              AND restaurant_id = :restaurantId
//        )
//        """;

        String sql = """
        SELECT
            CASE
                WHEN COUNT(*) > 0 THEN 1
                ELSE 0
            END
        FROM user_like_restaurant
        WHERE user_id = :userId
          AND restaurant_id = :restaurantId
        """; //추후 exist랑 성능비교예정

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("restaurantId", restaurantId);
        return template.queryForObject(sql, params, Integer.class);

    }

    public Integer getTotalLikeCount(Long restaurantId) {

        String sql = """
                SELECT COUNT(*)
                FROM user_like_restaurant u
                WHERE u.restaurant_id =:restaurantId
                """;

        Map<String, Long> param = Map.of("restaurantId", restaurantId);

        return template.queryForObject(sql, param, Integer.class);

    }
}
