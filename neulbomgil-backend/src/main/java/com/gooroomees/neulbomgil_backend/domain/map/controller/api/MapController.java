package com.gooroomees.neulbomgil_backend.domain.map.controller.api;

import com.gooroomees.neulbomgil_backend.domain.map.dto.request.FacilitySearchRequest;
import com.gooroomees.neulbomgil_backend.domain.map.dto.request.MarkerRequest;
import com.gooroomees.neulbomgil_backend.domain.map.dto.request.NearbyParkRequest;
import com.gooroomees.neulbomgil_backend.domain.map.dto.response.FacilityDetailResponse;
import com.gooroomees.neulbomgil_backend.domain.map.dto.response.FacilityMarkerResponse;
import com.gooroomees.neulbomgil_backend.domain.map.dto.response.FacilityResponse;
import com.gooroomees.neulbomgil_backend.domain.map.dto.response.NearParkResponse;
import com.gooroomees.neulbomgil_backend.domain.map.service.MapService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;

    @Operation(
            summary = "지도 시설 마커 조회"
    )
    @GetMapping("/facilities/markers")
    public ResponseEntity<List<FacilityMarkerResponse>> getFacilityMarkers(@ModelAttribute MarkerRequest request) {
        return ResponseEntity.ok(mapService.getFacilityMarkers(request));
    }

    @Operation(
            summary = "시설 목록 조회",
            description = "마지막 시설의 id와 distance를 인자로 다음 페이지 호출"
    )
    @GetMapping("/facilities")
    public ResponseEntity<List<FacilityResponse>> getFacilities(@ParameterObject @ModelAttribute FacilitySearchRequest request) {
        return ResponseEntity.ok(mapService.getFacilities(request));
    }

    @Operation(
            summary = "특정 시설 상세정보 조회"
    )
    @GetMapping("/facilities/{facilityId}")
    public ResponseEntity<FacilityDetailResponse> getFacilityDetail(@PathVariable String facilityId) {
        try {
            FacilityDetailResponse response = mapService.getFacilityDetail(facilityId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "특정 시설 일정 거리내에 있는 공원들 조회",
            description = "radius를 인자값으로 전달해주는데 기본값은 3(3km)"
    )
    @GetMapping("/facilities/markers/{facilityId}/nearby-parks")
    public ResponseEntity<List<NearParkResponse>> getNearbyParks(
            @PathVariable String facilityId,
            @RequestParam(defaultValue = "3") Double radius) {

        NearbyParkRequest request = new NearbyParkRequest(facilityId, radius);
        return ResponseEntity.ok(mapService.getNearbyParks(request));
    }
}