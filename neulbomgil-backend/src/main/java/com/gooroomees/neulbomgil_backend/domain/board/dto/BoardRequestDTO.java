package com.gooroomees.neulbomgil_backend.domain.board.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardRequestDTO {
    private String title;
    private String content;
}