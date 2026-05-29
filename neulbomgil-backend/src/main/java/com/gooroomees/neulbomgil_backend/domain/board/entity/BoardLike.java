package com.gooroomees.neulbomgil_backend.domain.board.entity;

import com.gooroomees.neulbomgil_backend.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "board_like",
        uniqueConstraints = @UniqueConstraint(columnNames = {"board_id", "user_id"}) // 한 유저가 같은 글에 중복 좋아요 방지
)
public class BoardLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public static BoardLike create(Board board, User user) {
        BoardLike like = new BoardLike();
        like.board = board;
        like.user = user;
        return like;
    }
}
