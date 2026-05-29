package com.gooroomees.neulbomgil_backend.domain.favorite.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FavoriteSearchRequest {
    @Min(0)
    private Double radius = 5000.0;

    public FavoriteSearchRequest(Double radius) {
        this.radius = (radius != null) ? radius : 5000.0;
    }
}