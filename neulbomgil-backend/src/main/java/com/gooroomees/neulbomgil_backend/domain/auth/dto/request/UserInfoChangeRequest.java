package com.gooroomees.neulbomgil_backend.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "회원 정보 수정")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserInfoChangeRequest {
    @Schema(description = "사용자 별명", example = "user123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
}