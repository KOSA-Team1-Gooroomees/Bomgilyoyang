package com.gooroomees.neulbomgil_backend.domain.board.controller;

import com.gooroomees.neulbomgil_backend.domain.auth.entity.UserAuth;
import com.gooroomees.neulbomgil_backend.domain.board.dto.BoardResponseDTO;
import com.gooroomees.neulbomgil_backend.domain.board.service.BoardService;
import com.gooroomees.neulbomgil_backend.domain.reply.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardViewController {

    private final BoardService boardService;
    private final ReplyService replyService;

    /* ===== 목록 (최신순) ===== */
    @GetMapping
    public String boardList(@RequestParam(defaultValue = "0") int page,
                            Model model) {
        model.addAttribute("boards", boardService.getAllBoards(page));
        model.addAttribute("sort", "recent");
        model.addAttribute("currentUrl", "/boards");
        return "board/list";
    }

    /* ===== 목록 (조회수순) ===== */
    @GetMapping("/sort/cnt")
    public String boardListByCnt(@RequestParam(defaultValue = "0") int page,
                                 Model model) {
        model.addAttribute("boards", boardService.getBoardsByViews(page));
        model.addAttribute("sort", "cnt");
        model.addAttribute("currentUrl", "/boards/sort/cnt");
        return "board/list";
    }

    /* ===== 목록 (댓글순) ===== */
    @GetMapping("/sort/replies")
    public String boardListByReplies(@RequestParam(defaultValue = "0") int page,
                                     Model model) {
        model.addAttribute("boards", boardService.getBoardsReplyCount(page));
        model.addAttribute("sort", "replies");
        model.addAttribute("currentUrl", "/boards/sort/replies");
        return "board/list";
    }

    /* ===== 검색 ===== */
    @GetMapping("/search")
    public String boardSearch(@RequestParam String keyword,
                              @RequestParam(defaultValue = "0") int page,
                              Model model) {
        model.addAttribute("boards", boardService.searchBoard(keyword, page));
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", "recent");
        model.addAttribute("currentUrl", "/boards/search?keyword=" + keyword);
        return "board/list";
    }

    /* ===== 상세 조회 (로그인 필수) ===== */
    @GetMapping("/{boardId}")
    public String boardDetail(@PathVariable Long boardId,
                              @RequestParam(defaultValue = "0") int replyPage,
                              @AuthenticationPrincipal UserAuth userAuth,
                              Model model) {
        // 게시글 (조회수 +1, likedByMe 포함)
        BoardResponseDTO board = boardService.getOneBoard(boardId, userAuth);
        model.addAttribute("board", board);

        // 댓글 목록
        model.addAttribute("replies", replyService.getReplies(boardId, replyPage));

        // 현재 로그인 유저 ID (수정/삭제 버튼 표시 조건)
        model.addAttribute("currentUserId",
                userAuth != null ? userAuth.getUserId() : null);

        return "board/detail";
    }

    /* ===== 작성 페이지 (로그인 필수) ===== */
    @GetMapping("/new")
    public String boardWritePage() {
        return "board/write";
    }
}
