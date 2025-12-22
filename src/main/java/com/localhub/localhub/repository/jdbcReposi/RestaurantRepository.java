package com.localhub.localhub.repository.jdbcReposi;

import com.localhub.localhub.dto.request.RequestRestaurantDto;
import com.localhub.localhub.entity.restaurant.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RestaurantRepository {

    private final NamedParameterJdbcTemplate template;


    // 레스토랑 등록
    public int save(Long userId, RequestRestaurantDto dto) {

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
                    break_start_time
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
                    :breakStartTime
                )
                """;

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
                .addValue("hasBreakTime", dto.isHasBreakTime())
                .addValue("breakStartTime", dto.getBreakStartTime());

        return template.update(sql, params);
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
}