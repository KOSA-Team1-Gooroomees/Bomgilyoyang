package com.gooroomees.neulbomgil_backend.domain.map.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooroomees.neulbomgil_backend.domain.map.entity.Park;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParkResponse {

    @JsonProperty("공원명")
    private String name;

    @JsonProperty("공원구분")
    private String category;

    @JsonProperty("소재지지번주소")
    private String lotAddress;

    @JsonProperty("위도")
    private Double latitude;

    @JsonProperty("경도")
    private Double longitude;

    @JsonProperty("공원면적")
    private Double area;

    public Park toEntity() {
        return Park.builder()
                .name(this.name)
                .category(this.category)
                .lotAddress(this.lotAddress)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .area(this.area)
                .build();
    }
}