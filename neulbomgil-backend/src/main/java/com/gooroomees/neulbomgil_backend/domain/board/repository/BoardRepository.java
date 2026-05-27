package com.gooroomees.neulbomgil_backend.domain.board.repository;

import com.gooroomees.neulbomgil_backend.domain.auth.entity.UserAuth;
import com.gooroomees.neulbomgil_backend.domain.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
    Long countByUser(UserAuth user);
}
