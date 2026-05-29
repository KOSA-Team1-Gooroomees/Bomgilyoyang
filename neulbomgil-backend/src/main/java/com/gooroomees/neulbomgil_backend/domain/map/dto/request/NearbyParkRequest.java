package com.gooroomees.neulbomgil_backend.domain.map.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NearbyParkRequest {
    private String facilityId;
    private Double radius;
}