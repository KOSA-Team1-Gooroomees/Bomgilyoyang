package com.gooroomees.neulbomgil_backend.domain.board.dto;
import com.gooroomees.neulbomgil_backend.domain.board.entity.Board;
import com.gooroomees.neulbomgil_backend.domain.board.entity.BoardFile;
import lombok.Getter;
import org.apache.tomcat.jni.FileInfo;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class BoardResponseDTO {
    private Long boardid;
    private Long userid;
    private String title;
    private String name;
    private String content;
    private int cnt;           // 조회수
    private int likeCnt;       // 좋아요 수
    private long replyCount;   // 댓글 수
    private boolean likedByMe; // 내가 좋아요 눌렀는지 여부
    private LocalDateTime createdAt; //업로드 날짜
    private LocalDateTime modifiedAt; // 수정 날짜
    private List<FileInfo> files;  // ← 파일 리스트 추가

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
        this.files      = List.of();  // 목록에서는 빈 리스트
    }
    // 상세 조회용 (likedByMe 포함)
    public BoardResponseDTO(Board board, long replyCount, boolean likedByMe, List<BoardFile> files) {
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
        this.files      = files.stream().map(FileInfo::new).toList();
    }
    // 파일 정보 내부 클래스
    @Getter
    public static class FileInfo {
        private Long fileid;
        private String originalName;
        private String fileUrl;
        private long fileSize;

        public FileInfo(BoardFile file) {
            this.fileid = file.getFileid();
            this.originalName = file.getOriginName();
            this.fileUrl = "/api/boards/files/" + file.getFileid();
            this.fileSize = file.getFileSize();
        }
    }
}
