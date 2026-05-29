package com.gooroomees.neulbomgil_backend.domain.board.repository;

import com.gooroomees.neulbomgil_backend.domain.auth.entity.User;
import com.gooroomees.neulbomgil_backend.domain.board.entity.Board;
import com.gooroomees.neulbomgil_backend.domain.board.entity.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {

    // 특정 유저가 특정 게시글에 좋아요를 눌렀는지 확인
    Optional<BoardLike> findByBoardAndUser(Board board, User user);

    // 특정 유저가 특정 게시글에 좋아요를 눌렀는지 여부
    boolean existsByBoardAndUser(Board board, User user);
    void deleteByBoard(Board board);
}
