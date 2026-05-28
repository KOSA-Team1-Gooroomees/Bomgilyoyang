package com.gooroomees.neulbomgil_backend.domain.board.service;

import com.gooroomees.neulbomgil_backend.domain.auth.entity.User;
import com.gooroomees.neulbomgil_backend.domain.board.dto.BoardRequestDTO;
import com.gooroomees.neulbomgil_backend.domain.board.dto.BoardResponseDTO;
import com.gooroomees.neulbomgil_backend.domain.board.entity.Board;
import com.gooroomees.neulbomgil_backend.domain.board.entity.BoardLike;
import com.gooroomees.neulbomgil_backend.domain.board.repository.BoardLikeRepository;
import com.gooroomees.neulbomgil_backend.domain.board.repository.BoardRepository;
import com.gooroomees.neulbomgil_backend.domain.reply.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final ReplyRepository replyRepository;   // 댓글 수 조회용
    private static final int PAGE_SIZE = 15;

    //게시글 없으면 예외처리
    // 게시글 존재 여부 확인
    private Board findBoard(Long boardId) {
        return boardRepository.findByIdWithUser(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
    }

    // 댓글 수를 포함한 BoardResponse 변환
    // 댓글 수 포함 DTO 변환 (목록용)
    private BoardResponseDTO toResponse(Board board) {
        long replyCount = replyRepository.countByBoard(board);
        return new BoardResponseDTO(board, replyCount);
    }

    // 최신순 (디폴트)
    public Page<BoardResponseDTO> getAllBoards(int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);  // 정렬은 쿼리에 포함
        return boardRepository.findAllWithUserOrderByCreatedAt(pageable)
                .map(this::toResponse);
    }

    // 조회수 높은순
    public Page<BoardResponseDTO> getBoardsByViews(int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        return boardRepository.findAllWithUserOrderByCnt(pageable)
                .map(this::toResponse);
    }
    // 댓글 많은순
    public Page<BoardResponseDTO> getBoardsReplyCount(int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        return boardRepository.findAllOrderByReplyCount(pageable).map(this::toResponse);
    }
    // 상세 조회 + 조회수 증가
    @Transactional
    public BoardResponseDTO getOneBoard(Long boardId, User user) {
        Board board = findBoard(boardId);
        board.increaseCnt();
        long replyCount = replyRepository.countByBoard(board);

        // 로그인한 경우에만 좋아요 여부 확인
        boolean likedByMe = (user != null)
                && boardLikeRepository.existsByBoardAndUser(board, user);

        return new BoardResponseDTO(board, replyCount, likedByMe);
    }

    //검색어 입력, 관련 글 가져오기
// 검색
    public Page<BoardResponseDTO> searchBoard(String keyword, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        return boardRepository.findByKeywordWithUser(keyword, pageable)
                .map(this::toResponse);
    }

    // 글 작성
    @Transactional
    public void createBoard(BoardRequestDTO dto, User user) {
        Board board = Board.create(user, dto.getTitle(), dto.getContent());
        boardRepository.save(board);
    }

    // 글 수정
    @Transactional
    public void updateBoard(BoardRequestDTO dto,Long boardId, User user) {
        Board board = findBoard(boardId);
        board.validateOwner(user);
        board.update(dto.getTitle(), dto.getContent());
    }

    // 변경 후 — 좋아요 삭제 후 댓글 삭제 후 게시글 삭제
    @Transactional
    public void deleteBoard(Long boardId, User user) {
        Board board = findBoard(boardId);
        board.validateOwner(user);
        // boardLikeRepository.deleteByBoard(board);  // ← 좋아요 먼저 삭제
        replyRepository.deleteByBoard(board);       // 댓글 삭제
        boardRepository.deleteById(boardId);        // 게시글 삭제
    }

    // 좋아요 토글 (눌렀으면 취소, 안 눌렀으면 추가)
    @Transactional
    public boolean toggleLike(Long boardId, User user) {
        Board board = findBoard(boardId);
        Optional<BoardLike> existing = boardLikeRepository.findByBoardAndUser(board, user);

        if (existing.isPresent()) {
            // 이미 좋아요 → 취소
            boardLikeRepository.delete(existing.get());
            board.decreaseLikeCnt();
            return false; // 좋아요 취소됨
        } else {
            // 좋아요 추가
            boardLikeRepository.save(BoardLike.create(board, user));
            board.increaseLikeCnt();
            return true; // 좋아요 추가됨
        }
    }
}