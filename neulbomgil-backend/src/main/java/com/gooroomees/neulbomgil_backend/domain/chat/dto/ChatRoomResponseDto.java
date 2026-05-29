package com.gooroomees.neulbomgil_backend.domain.chat.dto;


import java.time.LocalDateTime;

public record ChatRoomResponseDto(
        Long roomId,
        Long userId,
        String name,
        String lastMessage,
        LocalDateTime lastMessageAt,
        boolean unread

) {
}