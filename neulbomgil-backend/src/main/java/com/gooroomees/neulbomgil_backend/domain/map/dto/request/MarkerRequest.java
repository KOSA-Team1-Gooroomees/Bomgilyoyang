package com.gooroomees.neulbomgil_backend.domain.map.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MarkerRequest {
    private Double lat;
    private Double lon;
    private Double radius;
}