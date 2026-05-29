package com.gooroomees.neulbomgil_backend.domain.map.dto.response;

import com.gooroomees.neulbomgil_backend.domain.map.entity.Facility;
import lombok.Builder;

@Builder
public record FacilityMarkerResponse(
        String id,
        String facilityName,
        String newAddress,
        Double longitude,
        Double latitude
) {
    public static FacilityMarkerResponse from(Facility facility) {
        return FacilityMarkerResponse.builder()
                .id(facility.getId())
                .facilityName(facility.getFacilityName())
                .newAddress(facility.getNewAddress())
                .longitude(facility.getLongitude())
                .latitude(facility.getLatitude())
                .build();
    }
}