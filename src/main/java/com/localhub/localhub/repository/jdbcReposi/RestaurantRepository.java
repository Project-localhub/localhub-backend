package com.localhub.localhub.repository.jdbcReposi;

import com.localhub.localhub.dto.request.RequestRestaurantDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

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
            :ownerId,
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
                .addValue("ownerId", userId)
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



}
