package com.localhub.localhub.repository.jdbcReposi;

import com.localhub.localhub.dto.request.CreateReview;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Map;

@RequiredArgsConstructor
@Repository
public class RestaurantReviewRepositoryJDBC {

    private final NamedParameterJdbcTemplate template;


    public int save(Long userId, CreateReview createReview) {

        String sql = """
                INSERT INTO restaurant_review 
                (user_id,content,restaurant_id )
                VALUES
                (:user_id,:content,:restaurant_id)
                """;

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("user_id", userId);
        params.addValue("content", createReview.getContent());
        params.addValue("restaurant_id", createReview.getRestaurantId());

        template.update(sql, params,keyHolder);
        return keyHolder.getKey().intValue();
    }

    public int getTotalReviewCount(Long restaurantId) {


        String sql = """
                SELECT COUNT(*)
                FROM restaurant_review rev
                WHERE rev.restaurant_id = :restaurantId
                """;

        Map<String, Long> param = Map.of("restaurantId", restaurantId);
        return template.queryForObject(sql, param, Integer.class);
    }

    }


