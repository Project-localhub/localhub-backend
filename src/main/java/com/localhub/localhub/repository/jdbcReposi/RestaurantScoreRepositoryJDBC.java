package com.localhub.localhub.repository.jdbcReposi;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RestaurantScoreRepositoryJDBC {

    private final NamedParameterJdbcTemplate template;


    public Long save(Long userId, Long restaurantId, Integer score) {

        String sql = """
                INSERT
                INTO user_score_restaurant 
                (user_id, restaurant_id, score)
                VALUES
                (:userId, :restaurantId, :score)
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("restaurantId", restaurantId);
        params.addValue("score", score);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();


        template.update(sql, params, keyHolder);
        return keyHolder.getKey().longValue();

    }

}
