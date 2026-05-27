package com.gooroomees.neulbomgil_backend.domain.board.dto;
import com.gooroomees.neulbomgil_backend.domain.board.entity.Board;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class BoardResponseDTO {
    private Long boardid;
    private Long userid;
    private String title;
    private String name;
    private String content;
    private int cnt;           // 조회수
    private int likeCnt;       // 좋아요 수 (추가)
    private long replyCount;   // 댓글 수 (추가)
    private boolean likedByMe; // 내가 좋아요 눌렀는지 여부 (추가)
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public BoardResponseDTO(Board board, long replyCount) {
        this.boardid = board.getBoardid();
        this.userid = board.getUser().getUserId();   // UserAuth(getUser)에서 getuserId 꺼내기
        this.name = board.getUser().getName();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.cnt = board.getCnt();
        this.likeCnt = board.getLikeCnt();
        this.replyCount = replyCount;
        this.likedByMe = false;
        this.createdAt = board.getCreatedAt();
        this.modifiedAt = board.getModifiedAt();
    }
    // 상세 조회용 (likedByMe 포함)
    public BoardResponseDTO(Board board, long replyCount, boolean likedByMe) {
        this.boardid = board.getBoardid();
        this.userid = board.getUser().getUserId();
        this.name = board.getUser().getName();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.cnt = board.getCnt();
        this.likeCnt = board.getLikeCnt();
        this.replyCount = replyCount;
        this.likedByMe = likedByMe;
        this.createdAt = board.getCreatedAt();
        this.modifiedAt = board.getModifiedAt();
    }
}
