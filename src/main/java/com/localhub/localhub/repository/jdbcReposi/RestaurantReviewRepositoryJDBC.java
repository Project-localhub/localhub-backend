package com.localhub.localhub.repository.jdbcReposi;

import com.localhub.localhub.dto.request.CreateReview;
import com.localhub.localhub.dto.response.ResponseReviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
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

        template.update(sql, params,keyHolder,new String[]{"id"});
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


    public List<ResponseReviewDto> findByRestaurantId(Long restaurantId, long offset, int size) {

        String sql = """
                SELECT 
                
                rv.restaurant_id,
                COALESCE(usr.score,0) AS score,
                u.id,
                rv.content,
                u.username AS username,
                rv.created_at
               
                
                FROM restaurant_review rv
                
                LEFT JOIN users u
                on u.id = rv.user_id
                
                LEFT JOIN user_score_restaurant usr
                on usr.user_id = rv.user_id
                AND usr.restaurant_id = rv.restaurant_id
                
                WHERE rv.restaurant_id = :restaurantId
                
                LIMIT :size OFFSET :offset
                
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("offset", offset);
        params.addValue("size", size);
        params.addValue("restaurantId", restaurantId);

        return template.query(sql, params, (rs, roNum) ->

                ResponseReviewDto.builder()
                        .restaurantId(restaurantId)
                        .score(rs.getDouble("score"))
                        .userId(rs.getLong("id"))
                        .content(rs.getNString("content"))
                        .username(rs.getString("username"))
                        .createdAt(
                                rs.getTimestamp("created_at") == null
                                        ? null : rs.getTimestamp("created_at").toLocalDateTime()
                        )
                        .build()
        );
    }

    public Long countByRestaurantId(Long restaurantId) {

        String sql = """
                SELECT COUNT(*)
                FROM restaurant_review rv
                WHERE rv.restaurant_id = :restaurantId
                """;

        Map<String, Long> param = Map.of("restaurantId", restaurantId);

        return template.queryForObject(sql, param, Long.class);


    }
}


