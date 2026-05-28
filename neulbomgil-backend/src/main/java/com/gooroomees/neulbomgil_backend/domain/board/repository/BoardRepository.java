package com.gooroomees.neulbomgil_backend.domain.board.repository;

import com.gooroomees.neulbomgil_backend.domain.auth.entity.User;
import com.gooroomees.neulbomgil_backend.domain.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findByTitleContainingOrContentContaining(
            String title, String content, Pageable pageable);

    // COUNT(r) 게시글마다 댓글 개수를 세겠다는 의미
    // GROUP BY b 어떤 기준으로 셀건데? 게시글별로 그룹을 묶음
    //만약에 같은 댓글 수가 있다면 최신 게시글 순 반영
    @Query("""
    SELECT b
    FROM Board b
    LEFT JOIN Reply r ON r.board = b
    GROUP BY b
    ORDER BY COUNT(r) DESC, b.createdAt DESC
""")
    Page<Board> findAllOrderByReplyCount(Pageable pageable);

    Long countByUser(User user);
    // 최신순 사용할 JOIN FETCH
    @Query(value = "SELECT b FROM Board b JOIN FETCH b.user ORDER BY b.createdAt DESC",
            countQuery = "SELECT COUNT(b) FROM Board b")
    Page<Board> findAllWithUserOrderByCreatedAt(Pageable pageable);

    // 조회수순 사용할 JOIN FETCH
    @Query(value = "SELECT b FROM Board b JOIN FETCH b.user ORDER BY b.cnt DESC",
            countQuery = "SELECT COUNT(b) FROM Board b")
    Page<Board> findAllWithUserOrderByCnt(Pageable pageable);

    // 검색도 동일하게
    @Query(value = "SELECT b FROM Board b JOIN FETCH b.user WHERE b.title LIKE %:keyword% OR b.content LIKE %:keyword%",
            countQuery = "SELECT COUNT(b) FROM Board b WHERE b.title LIKE %:keyword% OR b.content LIKE %:keyword%")
    Page<Board> findByKeywordWithUser(@Param("keyword") String keyword, Pageable pageable);

    // BoardRepository 추가
    @Query("SELECT b FROM Board b JOIN FETCH b.user WHERE b.boardid = :boardId")
    Optional<Board> findByIdWithUser(@Param("boardId") Long boardId);

}
