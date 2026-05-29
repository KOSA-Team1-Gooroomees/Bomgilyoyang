package com.gooroomees.neulbomgil_backend.domain.map.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "시설 검색 요청 파라미터")
public class FacilitySearchRequest {
    @Schema(description = "검색어", example = "경기도")
    private String keyword;
    private Double userLat;
    private Double userLon;

    @Schema(description = "정렬 기준", example = "distance 또는 score")
    private String sort;

    @Schema(description = "마지막으로 조회된 시설 ID (페이징용)", example = "50")
    private String lastId;

    @Schema(description = "마지막으로 조회된 시설의 기준 값 (페이징용)", example = "1200.5 또는 5")
    private Double lastValue;

    @Schema(description = "한번에 가져올 데이터 수", example = "10")
    private Integer size = 10;  // 한 번에 가져올 데이터 수
}