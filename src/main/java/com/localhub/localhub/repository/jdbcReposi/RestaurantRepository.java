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
    public void save(RequestRestaurantDto requestRestaurantDto) {

        String sql = """
                INSERT INTO restaurant (name,businessNumber, description, category, phone,
                  address, latitude, longitude, openTime, closeTime, hasBreakTime, 
                  breakStartTime, breakEndTime)
                VALUES (:name, :businessNumber, :description, :category, :phone,
                       :address, :latitude, :longitude, :openTime, :closeTime, :hasBreakTime,
                       :breakStartTime, :breakEndTime)
                """;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", requestRestaurantDto.getName());
        params.addValue("businessNumber", requestRestaurantDto.getBusinessNumber());
        params.addValue("description", requestRestaurantDto.getDescription());


    }
}
