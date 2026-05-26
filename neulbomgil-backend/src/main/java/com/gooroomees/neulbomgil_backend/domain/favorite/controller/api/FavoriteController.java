package com.gooroomees.neulbomgil_backend.domain.favorite.controller.api;

import com.gooroomees.neulbomgil_backend.domain.auth.entity.User;
import com.gooroomees.neulbomgil_backend.domain.favorite.dto.request.FavoriteDeleteRequest;
import com.gooroomees.neulbomgil_backend.domain.favorite.dto.request.FavoriteRequest;
import com.gooroomees.neulbomgil_backend.domain.favorite.dto.response.FavoriteResponse;
import com.gooroomees.neulbomgil_backend.domain.favorite.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(
            summary = "즐겨찾기 추가"
    )
    @PostMapping
    public ResponseEntity<Long> addFavorite(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody FavoriteRequest request) {
        return ResponseEntity.ok(favoriteService.saveFavorite(user.getUserId(), request));
    }

    @Operation(
            summary = "특정 사용자의 즐겨찾기 시설 조회"
    )
    @GetMapping("/me")
    public ResponseEntity<List<FavoriteResponse>> getFavorites(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(favoriteService.getUserFavoritesWithDetail(user.getUserId()));
    }

    @Operation(
            summary = "즐겨찾기 삭제"
    )
    @DeleteMapping("/me")
    public ResponseEntity<Void> removeFavorite(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody FavoriteDeleteRequest request) {
        favoriteService.deleteFavorite(user.getUserId(), request);
        return ResponseEntity.noContent().build();
    }
}