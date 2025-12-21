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


    //레스토랑 등록
    public int save(Long userId,RequestRestaurantDto requestRestaurantDto) {

        String sql = """
                INSERT INTO restaurant (name,businessNumber, description, category, phone,
                  address, latitude, longitude, openTime, closeTime, hasBreakTime, 
                  breakStartTime, breakEndTime,userId)
                VALUES (:name, :businessNumber, :description, :category, :phone,
                       :address, :latitude, :longitude, :openTime, :closeTime, :hasBreakTime,
                       :breakStartTime, :breakEndTime, :userId)
                """;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", requestRestaurantDto.getName());
        params.addValue("businessNumber", requestRestaurantDto.getBusinessNumber());
        params.addValue("description", requestRestaurantDto.getDescription());
        params.addValue("category", requestRestaurantDto.getCategory());
        params.addValue("phone", requestRestaurantDto.getPhone());
        params.addValue("address", requestRestaurantDto.getAddress());
        params.addValue("latitude", requestRestaurantDto.getLatitude());
        params.addValue("longitude", requestRestaurantDto.getLongitude());
        params.addValue("openTime", requestRestaurantDto.getOpenTime());
        params.addValue("closeTime", requestRestaurantDto.getCloseTime());
        params.addValue("hasBreakTime", requestRestaurantDto.isHasBreakTime());
        params.addValue("breakStartTime", requestRestaurantDto.getBreakStartTime());
        params.addValue("breakEndTime", requestRestaurantDto.getBreakEndTime());
        params.addValue("userId", userId);

        int result = template.update(sql, params);
        return result;
    }
}
