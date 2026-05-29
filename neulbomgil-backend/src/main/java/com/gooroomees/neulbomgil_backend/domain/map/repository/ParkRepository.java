package com.gooroomees.neulbomgil_backend.domain.map.repository;

import com.gooroomees.neulbomgil_backend.domain.map.entity.Park;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface ParkRepository extends JpaRepository<Park, Integer> {

    @Query(value = "SELECT * FROM park p " +
            "WHERE (6371 * acos(cos(radians(:lat)) * cos(radians(p.latitude)) * cos(radians(p.longitude) - radians(:lon)) " +
            "+ sin(radians(:lat)) * sin(radians(p.latitude)))) <= :radius " +
            "ORDER BY (6371 * acos(cos(radians(:lat)) * cos(radians(p.latitude)) * cos(radians(p.longitude) - radians(:lon)) " +
            "+ sin(radians(:lat)) * sin(radians(p.latitude)))) ASC",
            nativeQuery = true)
    List<Park> findNearbyParks(@Param("lat") Double lat,
                               @Param("lon") Double lon,
                               @Param("radius") Double radius);

    // 반경 3km 내 공원 개수와 면적 합계를 조회
    @Query(value = "SELECT COUNT(*) as count, SUM(p.area) as totalArea FROM park p " +
            "WHERE ST_Distance_Sphere(point(p.longitude, p.latitude), point(:lon, :lat)) <= 3000",
            nativeQuery = true)
    Map<String, Object> getParkStatsWithinRadius(@Param("lat") double lat, @Param("lon") double lon);
}
