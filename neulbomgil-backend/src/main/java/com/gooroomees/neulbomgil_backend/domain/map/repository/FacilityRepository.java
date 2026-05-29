package com.gooroomees.neulbomgil_backend.domain.map.repository;

import com.gooroomees.neulbomgil_backend.domain.map.dto.response.FacilityDetailResponse;
import com.gooroomees.neulbomgil_backend.domain.map.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, String>, FacilityRepositoryCustom {

    @Query(value = "SELECT *, (6371 * acos(cos(radians(:lat)) * cos(radians(f.latitude)) " +
            "* cos(radians(f.longitude) - radians(:lon)) + sin(radians(:lat)) " +
            "* sin(radians(f.latitude)))) AS distance " +
            "FROM facility f " +
            "HAVING distance <= :radius " +
            "ORDER BY distance", nativeQuery = true)
    List<Facility> findFacilitiesWithinDistance(@Param("lat") Double lat,
                                                @Param("lon") Double lon,
                                                @Param("radius") Double radius);
}
