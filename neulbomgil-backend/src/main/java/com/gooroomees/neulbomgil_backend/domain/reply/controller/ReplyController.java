package com.gooroomees.neulbomgil_backend.domain.reply.controller;

import com.gooroomees.neulbomgil_backend.domain.auth.entity.CustomUserDetails;
import com.gooroomees.neulbomgil_backend.domain.auth.entity.User;
import com.gooroomees.neulbomgil_backend.domain.reply.dto.ReplyRequestDTO;
import com.gooroomees.neulbomgil_backend.domain.reply.dto.ReplyResponseDTO;
import com.gooroomees.neulbomgil_backend.domain.reply.service.ReplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "댓글", description = "댓글 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards/{boardId}/replies")
public class ReplyController {
    private final ReplyService replyService;

    // 댓글 목록 조회
    @Operation(summary = "댓글 목록 조회",
            description = "특정 게시글에 달린 댓글 목록을 최신 순으로 페이지네이션하여 조회합니다.")
    @GetMapping
    public ResponseEntity<Page<ReplyResponseDTO>> getReplies(
            @PathVariable Long boardId,
            @RequestParam(defaultValue = "0") int page){
        return ResponseEntity.ok(replyService.getReplies(boardId, page));
    }
    // 댓글 작성
    @Operation(summary = "댓글 작성",
            description = "특정 게시글에 댓글을 작성합니다. JWT 토큰이 필요합니다.")
    @PostMapping
    public ResponseEntity<Void> createReply(
            @PathVariable Long boardId,
            @RequestBody ReplyRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();  // ← 세션에서 꺼내야 함
        replyService.createReply(boardId, dto, user);
        return ResponseEntity.ok().build();
    }
    // 댓글 수정
    @Operation(summary = "댓글 수정",
            description = "본인이 작성한 댓글을 수정합니다. 작성자 본인만 수정 가능합니다.")
    @PutMapping("/{replyId}")
    public ResponseEntity<Void> updateReply(
            @PathVariable Long boardId,
            @PathVariable Long replyId,
            @RequestBody ReplyRequestDTO dto,
            @AuthenticationPrincipal User user){
        replyService.updateReply(boardId, replyId, dto, user);
        return ResponseEntity.ok().build();
    }
    // 댓글 삭제
    @Operation(summary = "댓글 삭제",
            description = "본인이 작성한 댓글을 삭제합니다. 작성자 본인만 삭제 가능합니다.")
    @DeleteMapping("/{replyId}")
    public ResponseEntity<Void> deleteReply(
            @PathVariable Long boardId,
            @PathVariable Long replyId,
            @AuthenticationPrincipal User user) {
        replyService.deleteReply(boardId, replyId, user);
        return ResponseEntity.noContent().build();
    }
}
