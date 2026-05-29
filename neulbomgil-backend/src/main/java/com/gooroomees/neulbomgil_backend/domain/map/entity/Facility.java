package com.gooroomees.neulbomgil_backend.domain.map.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facility {

    @Id
    private String id;

    private String facilityName;      // 시설 이름
    private String facilityTel;       // 시설 전화번호
    private String categoryName;      // 카테고리
    private String oldAddress;        // 구주소
    private String newAddress;        // 신주소

    private Double longitude;         // 좌표 X
    private Double latitude;          // 좌표 Y

    private Integer facilityScore; // 시설 점수(1~5)
    private String facilityImage; // 시설 이미지
    private Integer capacityCnt; // 수용인원
    private Integer currentCnt; // 입소현황
}
