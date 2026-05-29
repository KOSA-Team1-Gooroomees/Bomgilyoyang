package com.gooroomees.neulbomgil_backend.domain.board.controller;

import com.gooroomees.neulbomgil_backend.domain.auth.entity.CustomUserDetails;
import com.gooroomees.neulbomgil_backend.domain.auth.entity.User;
import com.gooroomees.neulbomgil_backend.domain.board.dto.BoardRequestDTO;
import com.gooroomees.neulbomgil_backend.domain.board.dto.BoardResponseDTO;
import com.gooroomees.neulbomgil_backend.domain.board.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Tag(name = "게시판", description = "게시판 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardController {
    private final BoardService boardService;

    // 최신순 (디폴트)
    @Operation(summary = "게시글 목록 조회 (최신순)",
            description = "게시글 전체 목록을 최신순으로 페이지네이션하여 조회합니다.")
    @GetMapping
    public ResponseEntity<Page<BoardResponseDTO>> getAllBoards(
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(boardService.getAllBoards(page));
    }

    // 조회수 높은순
    @Operation(summary = "게시글 목록 조회 (조회수순)",
            description = "게시글 전체 목록을 조회수 높은 순으로 페이지네이션하여 조회합니다.")
    @GetMapping("/sort/cnt")
    public ResponseEntity<Page<BoardResponseDTO>> getBoardsByViews(
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(boardService.getBoardsByViews(page));
    }

    //댓글 많은 순
    @Operation(summary = "댓글 많은 순 조회",
            description = "댓글이 많은 순으로 페이지네이션하여 조회합니다.")
    @GetMapping("/sort/replies")
    public ResponseEntity<Page<BoardResponseDTO>>getBoardsReplyCount(@RequestParam(defaultValue = "0")int page)
    {
        return ResponseEntity.ok(boardService.getBoardsReplyCount(page));
    }

    // 단건 조회 (userAuth nullable - 비로그인도 조회 가능)
    @Operation(summary = "게시글 단건 조회", description = "조회 시 조회수 +1. 로그인 시 좋아요 여부 포함.")
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponseDTO> getOneBoard(
            @PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = (userDetails != null) ? userDetails.getUser() : null; // 비로그인 허용
        return ResponseEntity.ok(boardService.getOneBoard(boardId, user));
    }

    // 검색
    @Operation(summary = "게시글 검색",
            description = "제목 또는 내용에 키워드가 포함된 게시글을 최신순으로 조회합니다.")
    @GetMapping("/search")
    public ResponseEntity<Page<BoardResponseDTO>> searchBoard(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(boardService.searchBoard(keyword, page));
    }

    // 글 작성
    @Operation(summary = "게시글 작성",
            description = "로그인한 사용자가 새 게시글을 작성합니다. JWT 토큰이 필요합니다.")
    @PostMapping(value = "/inserts", consumes = "multipart/form-data")
    public ResponseEntity<Void> createBoard(
            @RequestPart("data") BoardRequestDTO dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        boardService.createBoard(dto, user, files);
        return ResponseEntity.ok().build();
    }

    // 글 수정
    @Operation(summary = "게시글 수정",
            description = "본인이 작성한 게시글을 수정합니다. 작성자 본인만 수정 가능합니다.")
    @PutMapping(value = "/{boardId}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateBoard(
            @PathVariable Long boardId,
            @RequestPart("data") BoardRequestDTO dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        boardService.updateBoard(dto, boardId, user, files);
        return ResponseEntity.ok().build();
    }

    // 글 삭제
    @Operation(summary = "게시글 삭제",
            description = "본인이 작성한 게시글을 삭제합니다. 작성자 본인만 삭제 가능합니다.")
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(
            @PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        boardService.deleteBoard(boardId, user);
        return ResponseEntity.noContent().build();
    }
    // 좋아요 토글
    @Operation(summary = "좋아요 토글", description = "좋아요 추가/취소. JWT 토큰 필요.")
    @PostMapping("/{boardId}/likes")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        boolean liked = boardService.toggleLike(boardId, user);
        return ResponseEntity.ok(Map.of("liked", liked));
    }
    // 이미지 미리보기용 — Content-Disposition 없이 반환
    @GetMapping("/files/{fileId}/preview")
    public ResponseEntity<Resource> previewFile(@PathVariable Long fileId) throws Exception {
        Resource resource = boardService.downloadFile(fileId);
        // Content-Type 자동 감지
        String contentType = Files.probeContentType(
                Paths.get(boardService.getFilePath(fileId))
        );
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                .body(resource);
    }

    // 파일 다운로드
    @Operation(summary = "첨부파일 다운로드")
    @GetMapping("/files/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) throws Exception {
        Resource resource = boardService.downloadFile(fileId);
        String originalName = boardService.getOriginalFileName(fileId);
        String encodedName = URLEncoder.encode(originalName, StandardCharsets.UTF_8).replace("+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedName)
                .body(resource);
    }
    //수정에서 파일 삭제
    @DeleteMapping("/files/{fileId}")
    public ResponseEntity<Void> deleteFile(
            @PathVariable Long fileId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        boardService.deleteFile(fileId, userDetails.getUser());
        return ResponseEntity.noContent().build();
    }
}