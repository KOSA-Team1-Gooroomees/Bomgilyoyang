package com.gooroomees.neulbomgil_backend.domain.map.service;

import com.gooroomees.neulbomgil_backend.domain.map.dto.response.FacilityDetailResponse;
import com.gooroomees.neulbomgil_backend.domain.map.dto.response.FacilityMarkerResponse;
import com.gooroomees.neulbomgil_backend.domain.map.dto.request.FacilitySearchRequest;
import com.gooroomees.neulbomgil_backend.domain.map.dto.request.MarkerRequest;
import com.gooroomees.neulbomgil_backend.domain.map.dto.request.NearbyParkRequest;
import com.gooroomees.neulbomgil_backend.domain.map.dto.response.FacilityResponse;
import com.gooroomees.neulbomgil_backend.domain.map.dto.response.NearParkResponse;
import com.gooroomees.neulbomgil_backend.domain.map.entity.Facility;
import com.gooroomees.neulbomgil_backend.domain.map.entity.Park;
import com.gooroomees.neulbomgil_backend.domain.map.repository.FacilityRepository;
import com.gooroomees.neulbomgil_backend.domain.map.repository.ParkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MapService {

    private final FacilityRepository facilityRepository;
    private final ParkRepository parkRepository;

    public List<FacilityMarkerResponse> getFacilityMarkers(MarkerRequest request) {
        return facilityRepository.findFacilitiesWithinDistance(
                        request.getLat(),
                        request.getLon(),
                        request.getRadius()
                )
                .stream()
                .map(FacilityMarkerResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FacilityResponse> getFacilities(FacilitySearchRequest request) {
        return facilityRepository.searchByRegionCursor(request);
    }

    public FacilityDetailResponse getFacilityDetail(String facilityId) {
        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new IllegalArgumentException("해당 시설이 존재하지 않습니다. ID: " + facilityId));

        return FacilityDetailResponse.from(facility);
    }

    public List<NearParkResponse> getNearbyParks(NearbyParkRequest request) {
        Facility facility = facilityRepository.findById(request.getFacilityId())
                .orElseThrow(() -> new IllegalArgumentException("해당 시설이 없습니다."));

        List<Park> parks = parkRepository.findNearbyParks(
                facility.getLatitude(),
                facility.getLongitude(),
                request.getRadius()
        );

        return parks.stream()
                .map(NearParkResponse::from)
                .toList();
    }
}