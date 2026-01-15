package com.localhub.localhub.repository.jpaReposi;

import com.localhub.localhub.dto.response.ResponseRestaurantListDto;
import com.localhub.localhub.entity.restaurant.*;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class RestaurantQueryDslRepository {

    private final EntityManager em;

    private  JPAQueryFactory queryFactory;


    @PostConstruct
    void init() {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Page<ResponseRestaurantListDto> findAllWithScores(
            Pageable pageable,
            String category,
            String divide
    ) {
        QRestaurant r = QRestaurant.restaurant;
        QRestaurantScore rs = QRestaurantScore.restaurantScore;
        QRestaurantReview rv = QRestaurantReview.restaurantReview;
        QUserLikeRestaurant uls = QUserLikeRestaurant.userLikeRestaurant;
        QRestaurantImages rim = QRestaurantImages.restaurantImages;

        List<ResponseRestaurantListDto> content =
                queryFactory
                        .select(
                                Projections.constructor(
                                        ResponseRestaurantListDto.class,
                                        r.id,
                                        r.name,
                                        r.category,
                                        rs.score.avg().coalesce(0.0),
                                        rv.id.countDistinct(),
                                        uls.id.countDistinct(),
                                        rim.imageKey,
                                        r.latitude,
                                        r.longitude
                                )
                        )
                        .from(r)
                        .leftJoin(rs).on(rs.restaurantId.eq(r.id))
                        .leftJoin(rv).on(rv.restaurantId.eq(r.id))
                        .leftJoin(uls).on(uls.restaurantId.eq(r.id))
                        .leftJoin(rim).on(
                                rim.restaurantId.eq(r.id)
                                        .and(rim.sortOrder.eq(1))
                        )
                        .where(
                                categoryEq(category, r),
                                divideEq(divide, r)
                        )
                        .groupBy(
                                r.id,
                                r.name,
                                r.category,
                                rim.imageKey
                        )
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        Long total =
                queryFactory
                        .select(r.id.countDistinct())
                        .from(r)
                        .where(
                                categoryEq(category, r),
                                divideEq(divide, r)
                        )
                        .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    private BooleanExpression categoryEq(String category, QRestaurant r) {
        return category == null ? null : r.category.eq(Category.valueOf(category));
    }

    private BooleanExpression divideEq(String divide, QRestaurant r) {
        return divide == null ? null : r.divide.eq(divide);
    }


}
