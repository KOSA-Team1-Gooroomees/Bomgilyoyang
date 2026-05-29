package com.gooroomees.neulbomgil_backend.domain.map.service;

import com.gooroomees.neulbomgil_backend.domain.map.dto.response.VWorldResponse;
import com.gooroomees.neulbomgil_backend.domain.map.entity.Facility;
import com.gooroomees.neulbomgil_backend.domain.map.repository.FacilityRepository;
import com.gooroomees.neulbomgil_backend.domain.map.repository.ParkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FacilityDataInitService {

    private final FacilityRepository facilityRepository;
    private final ParkRepository parkRepository;
    private final RestTemplate restTemplate;

    @Value("${vworld.api.key}")
    private String API_KEY;

    private final String API_URL = "https://api.vworld.kr/req/data";

    private final String IMAGE_DIR_PATH = "src/main/resources/static/images/facility/";

    @Scheduled(cron = "0 0 4 * * *")
    public void refreshFacilities() {
        int page = 1;
        int size = 100;
        int totalPages = 1;

        log.info("VWorld 노인복지시설 데이터 갱신 시작...");

        try {
            while (page <= totalPages) {
                String url = UriComponentsBuilder.fromUriString(API_URL)
                        .queryParam("service", "data")
                        .queryParam("request", "GetFeature")
                        .queryParam("data", "LT_P_MGPRTFB")
                        .queryParam("key", API_KEY)
                        .queryParam("domain", "localhost:8080")
                        .queryParam("geomFilter", "BOX(0,0,200,200)")
                        .queryParam("size", size)
                        .queryParam("page", page)
                        .build().toUriString();

                VWorldResponse response = restTemplate.getForObject(url, VWorldResponse.class);
                if (response == null || response.getResponse() == null) break;

                VWorldResponse.ResponseData resBody = response.getResponse();

                if (page == 1 && resBody.getPage() != null) {
                    totalPages = Integer.parseInt(resBody.getPage().getTotal());
                    log.info("수집 대상: 총 {}페이지 ({}건)", totalPages, resBody.getRecord().getTotal());
                }

                if (resBody.getResult() != null && resBody.getResult().getFeatureCollection() != null) {
                    List<VWorldResponse.Feature> features = resBody.getResult().getFeatureCollection().getFeatures();
                    if (features != null && !features.isEmpty()) {
                        savePageData(features);
                        log.info("진행: {}/{} 페이지 완료", page, totalPages);
                    }
                }

                page++;
                Thread.sleep(200);
            }
        } catch (Exception e) {
            log.error("수집 중 예외 발생 (Page: {}): ", page, e);
        }
        log.info("모든 데이터 갱신이 완료되었습니다.");
    }

    @Transactional
    public void savePageData(List<VWorldResponse.Feature> features) {
        List<String> ids = features.stream().map(VWorldResponse.Feature::getId).toList();

        Map<String, Facility> existingMap = facilityRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Facility::getId, f -> f));

        List<Facility> facilitiesToSave = features.stream()
                .map(feature -> mapToEntity(feature, Optional.ofNullable(existingMap.get(feature.getId()))))
                .toList();

        facilityRepository.saveAll(facilitiesToSave);
    }

    private Facility mapToEntity(VWorldResponse.Feature feature, Optional<Facility> existing) {
        VWorldResponse.Properties props = feature.getProperties();
        List<Double> coords = feature.getGeometry().getCoordinates();
        double lon = coords.get(0);
        double lat = coords.get(1);

        String imagePath = determineImagePath(props.getFacilityName(), existing);

        return Facility.builder()
                .id(feature.getId())
                .facilityName(props.getFacilityName())
                .facilityTel(props.getFacilityTel())
                .categoryName(props.getCategoryName())
                .oldAddress(props.getOldAddress())
                .newAddress(props.getNewAddress())
                .longitude(lon)
                .latitude(lat)
                .capacityCnt(existing.map(Facility::getCapacityCnt).orElse(40))
                .currentCnt(existing.map(Facility::getCurrentCnt).orElse(0))
                .facilityImage(imagePath)
                .facilityScore(calculateFacilityScore(lat, lon))
                .build();
    }

    private final String WEB_PATH_PREFIX = "/images/facility/";
    private final String DEFAULT_IMAGE = "/images/facility.png";

    private String determineImagePath(String facilityName, Optional<Facility> existing) {
        if (existing.isPresent()) {
            String currentImg = existing.get().getFacilityImage();
            if (currentImg != null && currentImg.startsWith("/")) {
                return currentImg;
            }
        }

        if (facilityName == null || facilityName.isBlank()) {
            return DEFAULT_IMAGE;
        }

        String cleanedName = facilityName.replaceAll("\\s+", "");
        String webpFileName = cleanedName + ".webp";

        File imageFile = new File(IMAGE_DIR_PATH + webpFileName);
        if (imageFile.exists()) {
            return WEB_PATH_PREFIX + webpFileName;
        }

        return DEFAULT_IMAGE;
    }

    private Integer calculateFacilityScore(double lat, double lon) {
        Map<String, Object> stats = parkRepository.getParkStatsWithinRadius(lat, lon);
        long count = ((Number) stats.getOrDefault("count", 0)).longValue();
        double totalArea = stats.get("totalArea") != null ? ((Number) stats.get("totalArea")).doubleValue() : 0.0;

        double countScore = Math.min(count / 10.0, 1.0) * 2.5;
        double areaScore = Math.min(totalArea / 100000.0, 1.0) * 2.5;

        return Math.max((int) Math.round(countScore + areaScore), 1);
    }
}