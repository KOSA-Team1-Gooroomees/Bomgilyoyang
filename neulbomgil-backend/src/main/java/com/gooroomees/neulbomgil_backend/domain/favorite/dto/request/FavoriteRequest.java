package com.gooroomees.neulbomgil_backend.domain.favorite.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FavoriteRequest {
    @NotBlank(message = "시설 ID는 필수입니다.")
    private String facilityId;
}