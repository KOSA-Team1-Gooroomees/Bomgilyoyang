package com.gooroomees.neulbomgil_backend.domain.map.repository;

import com.gooroomees.neulbomgil_backend.domain.map.dto.request.FacilitySearchRequest;
import com.gooroomees.neulbomgil_backend.domain.map.dto.response.FacilityResponse;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.gooroomees.neulbomgil_backend.domain.map.entity.QFacility.facility;

@Repository
@RequiredArgsConstructor
public class FacilityRepositoryImpl implements FacilityRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<FacilityResponse> searchByRegionCursor(FacilitySearchRequest request) {
        // 1. 거리 계산 수식 정의
        NumberExpression<Double> distanceExpression = Expressions.numberTemplate(Double.class,
                "6371 * acos(cos(radians({0})) * cos(radians({1}.latitude)) * cos(radians({1}.longitude) - radians({2})) + sin(radians({0})) * sin(radians({1}.latitude)))",
                request.getUserLat(), facility, request.getUserLon());

        return queryFactory
                .select(Projections.fields(FacilityResponse.class,
                        facility.id,
                        facility.facilityName,
                        facility.facilityTel,
                        facility.categoryName,
                        facility.oldAddress,
                        facility.newAddress,
                        facility.longitude,
                        facility.latitude,
                        facility.facilityScore,
                        facility.facilityImage,
                        facility.capacityCnt,
                        facility.currentCnt,
                        distanceExpression.as("distance") // 수식 결과를 distance 필드 이름으로 매핑
                ))
                .from(facility)
                .where(
                        keywordContains(request.getKeyword()),
                        cursorCondition(request, distanceExpression)
                )
                .orderBy(createOrderSpecifier(request, distanceExpression))
                .limit(request.getSize())
                .fetch();
    }

    // 키워드 검색 조건 (주소 기준)
    private BooleanExpression keywordContains(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return facility.oldAddress.contains(keyword)
                .or(facility.newAddress.contains(keyword));
    }

    // 커서 기반 페이지네이션 조건 처리
    private BooleanExpression cursorCondition(FacilitySearchRequest request, NumberExpression<Double> distance) {
        if (request.getLastId() == null || request.getLastValue() == null) {
            return null; // 첫 페이지 요청 시 조건 없음
        }

        if ("score".equals(request.getSort())) {
            // 점수순(내림차순) 커서 로직
            // (점수 < 마지막점수) OR (점수 == 마지막점수 AND ID < 마지막ID)
            return facility.facilityScore.lt(request.getLastValue().intValue())
                    .or(facility.facilityScore.eq(request.getLastValue().intValue())
                            .and(facility.id.lt(request.getLastId())));
        } else {
            // 거리순(오름차순) 커서 로직
            // (거리 > 마지막거리) OR (거리 == 마지막거리 AND ID > 마지막ID)
            return distance.gt(request.getLastValue())
                    .or(distance.eq(request.getLastValue())
                            .and(facility.id.gt(request.getLastId())));
        }
    }

    // 정렬 조건 생성 (중복 방지를 위해 항상 ID를 보조 정렬 키로 사용)
    private OrderSpecifier<?>[] createOrderSpecifier(FacilitySearchRequest request, NumberExpression<Double> distance) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if ("score".equals(request.getSort())) {
            orders.add(new OrderSpecifier<>(Order.DESC, facility.facilityScore));
            orders.add(new OrderSpecifier<>(Order.DESC, facility.id));
        } else {
            // 기본은 거리순 정렬
            orders.add(new OrderSpecifier<>(Order.ASC, distance));
            orders.add(new OrderSpecifier<>(Order.ASC, facility.id));
        }
        return orders.toArray(new OrderSpecifier[0]);
    }
}