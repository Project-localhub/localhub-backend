package com.localhub.localhub.geo.repository;

import com.localhub.localhub.dto.response.StoreDistanceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PostgisStoreLocationRepository {

    @Qualifier("postgisJdbcTemplate")
    private final JdbcTemplate postgisJdbcTemplate;

    /**
     * 가게 위치 저장 (가게 생성 시 호출)
     */
    public Long saveLocation(Long storeId, BigDecimal lng, BigDecimal lat) {

        String sql = """
                INSERT INTO store_location (store_id, location)
                VALUES (?, ST_MakePoint(?, ?)::geography)
                RETURNING store_id
                """;
        Long id = postgisJdbcTemplate.queryForObject(
                sql,
                Long.class,
                storeId,
                lng,
                lat
        );
        return id;
    }

    /**
     * 기준 좌표 기준 반경 내 가게 + 거리 조회
     */
    public List<StoreDistanceDto> findNearbyStores(
            double lng,
            double lat,
            int radiusMeter,
            int limit
    ) {
        String sql = """
                SELECT
                    store_id,
                    ST_Distance(
                        location,
                        ST_MakePoint(?, ?)::geography
                    ) / 1000 AS distance_km
                FROM store_location
                WHERE ST_DWithin(
                    location,
                    ST_MakePoint(?, ?)::geography,
                    ?
                )
                ORDER BY distance_km
                LIMIT ?
                """;

        return postgisJdbcTemplate.query(
                sql,
                (rs, rowNum) -> new StoreDistanceDto(
                        rs.getLong("store_id"),
                        rs.getDouble("distance_km")
                ),
                lng, lat,
                lng, lat,
                radiusMeter,
                limit
        );
    }

    public List<StoreDistanceDto> findStoreDistancesByIds(
            List<Long> storeIds,
            BigDecimal lng,
            BigDecimal lat,
            int limit
    ) {
        if (storeIds.isEmpty()) {
            return List.of();
        }

        String placeholders = storeIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));

        String sql = """
                    SELECT
                        store_id,
                        ST_Distance(
                            location::geography,
                            ST_MakePoint(?, ?)::geography
                        ) / 1000 AS distance_km
                    FROM store_location
                    WHERE store_id IN (%s)
                    ORDER BY distance_km
                    LIMIT ?
                """.formatted(placeholders);

        List<Object> params = new ArrayList<>();
        params.add(lng);
        params.add(lat);
        params.addAll(storeIds);
        params.add(limit);

        return postgisJdbcTemplate.query(
                sql,
                params.toArray(),
                (rs, rowNum) -> new StoreDistanceDto(
                        rs.getLong("store_id"),
                        rs.getDouble("distance_km")
                )
        );
    }
}