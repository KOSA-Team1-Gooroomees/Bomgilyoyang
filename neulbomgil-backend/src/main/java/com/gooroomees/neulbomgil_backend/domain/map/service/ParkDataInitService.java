package com.gooroomees.neulbomgil_backend.domain.map.service;

import com.gooroomees.neulbomgil_backend.domain.map.dto.response.ParkResponse;
import com.gooroomees.neulbomgil_backend.domain.map.entity.Park;
import com.gooroomees.neulbomgil_backend.domain.map.repository.ParkRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParkDataInitService {

    private final ParkRepository parkRepository;
    private final ResourceLoader resourceLoader;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void initParkData() {
        log.info("공원 데이터 초기화 시작...");
        // 1. 기존 데이터 삭제
        parkRepository.deleteAllInBatch();

        try (InputStream is = resourceLoader.getResource("classpath:data/parks.json").getInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(is);
            JsonNode records = root.get("records");

            if (records != null && records.isArray()) {
                List<Park> chunk = new ArrayList<>();
                for (JsonNode node : records) {
                    ParkResponse dto = mapper.treeToValue(node, ParkResponse.class);
                    chunk.add(dto.toEntity());

                    // 1,000건 단위로 JDBC Bulk Insert 실행
                    if (chunk.size() >= 1000) {
                        batchInsert(chunk);
                        chunk.clear();
                    }
                }
                // 남은 데이터 처리
                if (!chunk.isEmpty()) {
                    batchInsert(chunk);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("공원 데이터 초기화 중 오류 발생", e);
        }
        log.info("공원 데이터 초기화 완료.");
    }

    // JdbcTemplate을 이용한 Bulk Insert
    private void batchInsert(List<Park> parks) {
        String sql = "INSERT INTO park (name, category, lot_address, latitude, longitude, area) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, parks, parks.size(), (ps, park) -> {
            ps.setString(1, park.getName());
            ps.setString(2, park.getCategory());
            ps.setString(3, park.getLotAddress());
            ps.setDouble(4, (park.getLatitude() != null) ? park.getLatitude() : 0.0);
            ps.setDouble(5, (park.getLongitude() != null) ? park.getLongitude() : 0.0);
            ps.setDouble(6, (park.getArea() != null) ? park.getArea() : 0.0);
        });
    }
}