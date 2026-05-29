package com.gooroomees.neulbomgil_backend.domain.admin.dto;

import com.gooroomees.neulbomgil_backend.domain.auth.entity.Status;


public record AdminUserResponseDto(
        Long userId,
        String name,
        String email,
        Long boardCount,
        Long replyCount,
        Status status,
        String createdAt
) {
}