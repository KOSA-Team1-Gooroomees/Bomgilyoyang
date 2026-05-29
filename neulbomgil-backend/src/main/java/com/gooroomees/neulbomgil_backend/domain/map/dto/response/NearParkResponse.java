package com.gooroomees.neulbomgil_backend.domain.map.dto.response;

import com.gooroomees.neulbomgil_backend.domain.map.entity.Park;
import lombok.Builder;

@Builder
public record NearParkResponse(
        Long id,
        String name,
        String category,
        String lotAddress,
        Double latitude,
        Double longitude,
        Double area
) {
    public static NearParkResponse from(Park park) {
        return NearParkResponse.builder()
                .id(park.getId())
                .name(park.getName())
                .category(park.getCategory())
                .lotAddress(park.getLotAddress())
                .latitude(park.getLatitude())
                .longitude(park.getLongitude())
                .area(park.getArea())
                .build();
    }
}
