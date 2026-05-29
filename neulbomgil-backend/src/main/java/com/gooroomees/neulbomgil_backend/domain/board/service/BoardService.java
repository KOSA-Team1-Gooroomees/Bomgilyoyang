package com.gooroomees.neulbomgil_backend.domain.board.service;

import com.gooroomees.neulbomgil_backend.domain.auth.entity.User;
import com.gooroomees.neulbomgil_backend.domain.board.dto.BoardRequestDTO;
import com.gooroomees.neulbomgil_backend.domain.board.dto.BoardResponseDTO;
import com.gooroomees.neulbomgil_backend.domain.board.entity.Board;
import com.gooroomees.neulbomgil_backend.domain.board.entity.BoardFile;
import com.gooroomees.neulbomgil_backend.domain.board.entity.BoardLike;
import com.gooroomees.neulbomgil_backend.domain.board.repository.BoardFileRepository;
import com.gooroomees.neulbomgil_backend.domain.board.repository.BoardLikeRepository;
import com.gooroomees.neulbomgil_backend.domain.board.repository.BoardRepository;
import com.gooroomees.neulbomgil_backend.domain.reply.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final BoardFileRepository boardFileRepository; //파일 업로드 용
    private final ReplyRepository replyRepository;   // 댓글 수 조회용
    private static final int PAGE_SIZE = 15;
    @Value("${file.upload-dir:uploads}")
    private String uploadDir;
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
        boolean likedByMe = (user != null)
                && boardLikeRepository.existsByBoardAndUser(board, user);
        List<BoardFile> files = boardFileRepository.findByBoard(board);  // ← 추가
        return new BoardResponseDTO(board, replyCount, likedByMe, files); // ← 파일 포함
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
    public void createBoard(BoardRequestDTO dto, User user, List<MultipartFile> files) {
        Board board = Board.create(user, dto.getTitle(), dto.getContent());
        boardRepository.save(board);
        saveFiles(board, files);  // ← 파일 저장 추가
    }

    // 글 수정
    @Transactional
    public void updateBoard(BoardRequestDTO dto,Long boardId, User user, List<MultipartFile> files) {
        Board board = findBoard(boardId);
        board.validateOwner(user);
        board.update(dto.getTitle(), dto.getContent());
        saveFiles(board, files);  // ← 새 파일 추가
    }

    // 변경 후 — 좋아요 삭제 후 댓글 삭제 후 게시글 삭제
    @Transactional
    public void deleteBoard(Long boardId, User user) {
        Board board = findBoard(boardId);
        board.validateOwner(user);

        // 첨부파일 실제 파일 삭제
        List<BoardFile> files = boardFileRepository.findByBoard(board);
        for (BoardFile file : files) {
            try {
                Files.deleteIfExists(Paths.get(file.getFilePath()));
            } catch (IOException e) {
                throw new RuntimeException("파일 삭제 중 오류가 발생했습니다.", e);
            }
        }
        boardFileRepository.deleteByBoard(board);
        boardLikeRepository.deleteByBoard(board);
        replyRepository.deleteByBoard(board);
        boardRepository.deleteById(boardId);
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
    // 수정에서 - 파일 삭제
    @Transactional
    public void deleteFile(Long fileId, User user) {
        BoardFile file = boardFileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("파일이 존재하지 않습니다."));
        // 게시글 소유자 확인
        file.getBoard().validateOwner(user);
        boardFileRepository.delete(file);
    }
    //파일 다운로드
    public Resource downloadFile(Long fileId) throws MalformedURLException{
        BoardFile boardFile = boardFileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("파일이 존재하지 않음"));
        Path filePath = Paths.get(boardFile.getFilePath());
        Resource resource = new UrlResource(filePath.toUri());
        if(!resource.exists()) throw new IllegalArgumentException("파일을 찾을 수 없습니다.");
        return resource;
    }
    //파일 이름 저장
    public String getOriginalFileName(Long fileId){
        return boardFileRepository.findById(fileId)
                .map(BoardFile::getOriginName)
                .orElse("File");
    }
    //이미지 처리 별도
    public String getFilePath(Long fileId) {
        return boardFileRepository.findById(fileId)
                .map(BoardFile::getFilePath)
                .orElseThrow(() -> new IllegalArgumentException("파일이 존재하지 않습니다."));
    }

    // 파일 저장 내부 메서드
    private void saveFiles(Board board, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return;
        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;
                String originalName = file.getOriginalFilename();
                String savedName = UUID.randomUUID() + "_" + originalName;
                Path savePath = uploadPath.resolve(savedName);
                file.transferTo(savePath.toAbsolutePath().toFile()); // ← 절대경로로 저장
                boardFileRepository.save(
                        BoardFile.create(board, originalName, savedName, savePath.toString(), file.getSize())
                );
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }
    }
}