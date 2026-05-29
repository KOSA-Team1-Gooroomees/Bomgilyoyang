package com.gooroomees.neulbomgil_backend.domain.favorite.service;

import com.gooroomees.neulbomgil_backend.domain.favorite.dto.request.FavoriteDeleteRequest;
import com.gooroomees.neulbomgil_backend.domain.favorite.dto.request.FavoriteRequest;
import com.gooroomees.neulbomgil_backend.domain.favorite.dto.request.FavoriteSearchRequest;
import com.gooroomees.neulbomgil_backend.domain.favorite.dto.response.FavoriteResponse;
import com.gooroomees.neulbomgil_backend.domain.favorite.entity.Favorite;
import com.gooroomees.neulbomgil_backend.domain.favorite.repository.FavoriteRepository;
import com.gooroomees.neulbomgil_backend.domain.map.entity.Facility;
import com.gooroomees.neulbomgil_backend.domain.map.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final FacilityRepository facilityRepository;

    @Transactional
    public Long saveFavorite(Long userId, FavoriteRequest request) {
        favoriteRepository.findByUserIdAndFacilityId(userId, request.getFacilityId())
                .ifPresent(f -> {
                    throw new IllegalStateException("이미 즐겨찾기한 시설입니다.");
                });

        Favorite favorite = Favorite.builder()
                .userId(userId)
                .facilityId(request.getFacilityId())
                .build();

        return favoriteRepository.save(favorite).getId();
    }

    public List<FavoriteResponse> getUserFavoritesWithDetail(Long userId) {
        List<Favorite> favorites = favoriteRepository.findAllByUserId(userId);
        List<String> facilityIds = favorites.stream()
                .map(Favorite::getFacilityId)
                .distinct()
                .toList();
        Map<String, Facility> facilityMap = facilityRepository.findAllById(facilityIds).stream()
                .collect(Collectors.toMap(Facility::getId, f -> f));

        return favorites.stream()
                .map(favorite -> FavoriteResponse.builder()
                        .id(favorite.getId())
                        .userId(favorite.getUserId())
                        .facilityId(favorite.getFacilityId())
                        .facility(facilityMap.get(favorite.getFacilityId()))
                        .build())
                .toList();
    }

    @Transactional
    public void deleteFavorite(Long userId, FavoriteDeleteRequest request) {
        favoriteRepository.deleteByUserIdAndFacilityId(userId, request.getFacilityId());
    }
}