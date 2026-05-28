package com.gooroomees.neulbomgil_backend.domain.reply.repository;

import com.gooroomees.neulbomgil_backend.domain.auth.entity.User;
import com.gooroomees.neulbomgil_backend.domain.board.entity.Board;
import com.gooroomees.neulbomgil_backend.domain.reply.entity.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    // 기존 메서드 교체
    @Query(value = "SELECT r FROM Reply r JOIN FETCH r.user WHERE r.board.boardid = :boardId",
            countQuery = "SELECT COUNT(r) FROM Reply r WHERE r.board.boardid = :boardId")
    Page<Reply> findByBoard_BoardidWithUser(@Param("boardId") Long boardId, Pageable pageable);

    @Query("SELECT r FROM Reply r JOIN FETCH r.user WHERE r.replyId = :replyId")
    Optional<Reply> findByIdWithUser(@Param("replyId") Long replyId);

    long countByBoard(Board board);
    void deleteByBoard(Board board); //댓글 먼저 삭제 후 게시글 삭제
    Long countByUser(User user);
}
