package com.localhub.localhub.repository.jdbcReposi;

import com.localhub.localhub.dto.request.CreateReview;
import com.localhub.localhub.entity.restaurant.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@RequiredArgsConstructor
@Repository
public class RestaurantReviewRepository {

    private final NamedParameterJdbcTemplate template;


    public int save(Long userId, CreateReview createReview) {

        String sql = """
                INSERT INTO restaurant_review 
                (user_id,content,restaurant_id )
                VALUES
                (:user_id,:content,:restaurant_id)
                """;


        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("user_id", userId);
        params.addValue("content", createReview.getContent());
        params.addValue("restaurant_id", createReview.getRestaurantId());

        int result = template.update(sql, params);
        return result;
    }



    }


