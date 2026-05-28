package com.gooroomees.neulbomgil_backend.domain.reply.repository;

import com.gooroomees.neulbomgil_backend.domain.auth.entity.User;
import com.gooroomees.neulbomgil_backend.domain.board.entity.Board;
import com.gooroomees.neulbomgil_backend.domain.reply.entity.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    Page<Reply> findByBoard_Boardid(Long boardId, Pageable pageable);
    long countByBoard(Board board);
    void deleteByBoard(Board board); //댓글 먼저 삭제 후 게시글 삭제
    Long countByUser(User user);
}
