package com.gooroomees.neulbomgil_backend.domain.board.repository;

import com.gooroomees.neulbomgil_backend.domain.auth.entity.UserAuth;
import com.gooroomees.neulbomgil_backend.domain.board.entity.Board;
import com.gooroomees.neulbomgil_backend.domain.board.entity.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    Optional<BoardLike> findByBoardAndUser(Board board, UserAuth user);
    boolean existsByBoardAndUser(Board board, UserAuth user);
    void deleteByBoard(Board board);  // ← 이 한 줄 추가
}