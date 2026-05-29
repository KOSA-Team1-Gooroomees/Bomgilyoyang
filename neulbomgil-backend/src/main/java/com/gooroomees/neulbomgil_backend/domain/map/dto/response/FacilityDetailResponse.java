package com.gooroomees.neulbomgil_backend.domain.map.dto.response;

import com.gooroomees.neulbomgil_backend.domain.map.entity.Facility;
import lombok.Builder;

@Builder
public record FacilityDetailResponse(
        String id,
        String facilityName,
        String facilityTel,
        String categoryName,
        String oldAddress,
        String newAddress,
        Double longitude,
        Double latitude,
        Integer facilityScore,
        String facilityImage,
        Integer capacityCnt,
        Integer currentCnt
) {
    public static FacilityDetailResponse from(Facility facility) {
        return FacilityDetailResponse.builder()
                .id(facility.getId())
                .facilityName(facility.getFacilityName())
                .facilityTel(facility.getFacilityTel())
                .categoryName(facility.getCategoryName())
                .oldAddress(facility.getOldAddress())
                .newAddress(facility.getNewAddress())
                .longitude(facility.getLongitude())
                .latitude(facility.getLatitude())
                .facilityScore(facility.getFacilityScore())
                .facilityImage(facility.getFacilityImage())
                .capacityCnt(facility.getCapacityCnt())
                .currentCnt(facility.getCurrentCnt())
                .build();
    }
}