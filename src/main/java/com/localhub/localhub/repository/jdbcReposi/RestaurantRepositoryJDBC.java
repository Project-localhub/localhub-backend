package com.localhub.localhub.repository.jdbcReposi;

import com.localhub.localhub.dto.request.RequestRestaurantDto;
import com.localhub.localhub.entity.restaurant.Category;
import com.localhub.localhub.entity.restaurant.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RestaurantRepositoryJDBC {

    private final NamedParameterJdbcTemplate template;


    // 레스토랑 등록
    public Long save(Long userId, RequestRestaurantDto dto) {

        String sql = """
                INSERT INTO restaurant (
                    owner_id,
                    name,
                    business_number,
                    description,
                    category,
                    phone,
                    address,
                    latitude,
                    longitude,
                    open_time,
                    close_time,
                    has_break_time,
                    break_start_time,
                    break_end_time
                )
                VALUES (
                    :owner_id,
                    :name,
                    :businessNumber,
                    :description,
                    :category,
                    :phone,
                    :address,
                    :latitude,
                    :longitude,
                    :open_time,
                    :close_time,
                    :hasBreakTime,
                    :breakStartTime,
                    :breakEndTime
                )
                """;

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();


        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("owner_id", userId)
                .addValue("name", dto.getName())
                .addValue("businessNumber", dto.getBusinessNumber())
                .addValue("description", dto.getDescription())
                .addValue("category", dto.getCategory()) // ENUM이면 name() 권장
                .addValue("phone", dto.getPhone())
                .addValue("address", dto.getAddress())
                .addValue("latitude", dto.getLatitude())
                .addValue("longitude", dto.getLongitude())
                .addValue("open_time", dto.getOpenTime())
                .addValue("close_time", dto.getCloseTime())
                .addValue("hasBreakTime", dto.getHasBreakTime())
                .addValue("breakStartTime", dto.getBreakStartTime())
                .addValue("breakEndTime", dto.getBreakEndTime());

        template.update(sql, params, keyHolder, new String[]{"id"});
        return keyHolder.getKey().longValue();
    }

    public Optional<Restaurant> findById(Long id) {

        String sql = """
                SELECT id,name,owner_id
                FROM restaurant
                WHERE id = :id
                """;

        Map<String, Long> param = Map.of("id", id);

        List<Restaurant> result = template.query(sql, param, (rs, roNum) ->
                Restaurant.builder()
                        .id(rs.getLong("id"))
                        .ownerId(rs.getLong("owner_id"))
                        .name(rs.getNString("name"))
                        .build()
        );

        return result.stream().findFirst();

    }

    public Optional<Restaurant> findByOwnerId(Long userId) {

        String sql = """
                SELECT * 
                FROM restaurant rs
                WHERE rs.owner_id = :userId
                """;
        Map<String, Long> param = Map.of("userId", userId);

        List<Restaurant> result = template.query(sql, param, (rs, roNum) ->
                Restaurant.builder()
                        .id(rs.getLong("id"))
                        .ownerId(rs.getLong("owner_id"))
                        .category(Category.valueOf(rs.getNString("cateogry")))
                        .address(rs.getNString("address"))
                        .name(rs.getNString("name"))
                        .openTime(rs.getTime("open_time").toLocalTime())
                        .closeTime(rs.getTime("close_time").toLocalTime())
                        .hasBreakTime(rs.getBoolean("has_break_time"))
                        .build()
        );

        return result.stream().findFirst();
    }

    public int deleteById(Long restaurantId) {

        String sql = """
                DELETE res
                FROM restaurant res
                WHERE id =:id
                """;
        Map<String, Long> param = Map.of("id", restaurantId);
        int result = template.update(sql, param);

        return result;

    }
}