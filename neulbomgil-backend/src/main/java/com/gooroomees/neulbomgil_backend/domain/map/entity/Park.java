package com.gooroomees.neulbomgil_backend.domain.map.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "park")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Park {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 공원명

    private String category; // 공원구분

    private String lotAddress; // 소재지지번주소

    private Double latitude; // 위도

    private Double longitude; // 경도

    private Double area; // 공원면적
}