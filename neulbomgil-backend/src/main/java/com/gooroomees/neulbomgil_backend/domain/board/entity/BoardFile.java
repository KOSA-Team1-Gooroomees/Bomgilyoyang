package com.gooroomees.neulbomgil_backend.domain.board.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    private String originName; //원본 파일명
    private String savedName;// 서버에 저장된 파일명
    private String filePath;//서버 내부 경로 (외부 노출 X)
    private long fileSize;//파일 크기

    public static BoardFile create(Board board, String originName, String savedName,
                                   String filePath, long fileSize){
        BoardFile file = new BoardFile();
        file.board = board;
        file.originName = originName;
        file.savedName = savedName;
        file.filePath = filePath;
        file.fileSize = fileSize;
        return file;
    }
}
