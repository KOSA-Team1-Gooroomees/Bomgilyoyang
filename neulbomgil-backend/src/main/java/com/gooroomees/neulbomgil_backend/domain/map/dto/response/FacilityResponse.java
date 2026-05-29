package com.gooroomees.neulbomgil_backend.domain.map.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityResponse {
    private String id;
    private String facilityName;
    private String facilityTel;
    private String categoryName;
    private String oldAddress;
    private String newAddress;
    private Double longitude;
    private Double latitude;
    private Integer facilityScore;
    private String facilityImage;
    private Integer capacityCnt;
    private Integer currentCnt;
    private Double distance;
}