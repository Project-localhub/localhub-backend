package com.localhub.localhub.geo.repository;

import com.localhub.localhub.dto.response.StoreDistanceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostgisStoreLocationRepository {

    private final JdbcTemplate postgisJdbcTemplate;

    /**
     * 가게 위치 저장 (가게 생성 시 호출)
     */
    public void saveLocation(Long storeId, BigDecimal lng, BigDecimal lat) {
        postgisJdbcTemplate.update(
                """
                INSERT INTO store_location (store_id, location)
                VALUES (?, ST_MakePoint(?, ?)::geography)
                """,
                storeId,
                lng,
                lat
        );
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

    public List<StoreDistanceDto> findNearbyStoreIds(
            BigDecimal lng,
            BigDecimal lat,
            int radiusMeter,
            int limit
    ) {
        return postgisJdbcTemplate.query(
                """
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
                """,
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
}
