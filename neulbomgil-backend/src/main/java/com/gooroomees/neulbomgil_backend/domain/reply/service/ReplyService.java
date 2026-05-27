package com.gooroomees.neulbomgil_backend.domain.reply.service;

import com.gooroomees.neulbomgil_backend.domain.auth.entity.User;
import com.gooroomees.neulbomgil_backend.domain.board.entity.Board;
import com.gooroomees.neulbomgil_backend.domain.board.repository.BoardRepository;
import com.gooroomees.neulbomgil_backend.domain.reply.dto.ReplyRequestDTO;
import com.gooroomees.neulbomgil_backend.domain.reply.dto.ReplyResponseDTO;
import com.gooroomees.neulbomgil_backend.domain.reply.entity.Reply;
import com.gooroomees.neulbomgil_backend.domain.reply.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;
    private static final int PAGE_SIZE = 5;

    //존재하지 않는 게시글
    private Board findBoard(Long boardId){
        return boardRepository.findById(boardId).orElseThrow(()
                -> new IllegalArgumentException("존재하지 않은 게시글입니다."));
    }
    //존재하지 않는 댓글
    private Reply findReply(Long replyId){
        return replyRepository.findById(replyId).orElseThrow(()
                -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
    }

    //댓글 목록 조회
    public Page<ReplyResponseDTO> getReplies(Long boardId, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());
        return replyRepository.findByBoard_Boardid(boardId, pageable).map(ReplyResponseDTO::new);
    }

    //댓글 작성
    @Transactional
    public void createReply(Long boardId, ReplyRequestDTO dto, User user) {
        Board board = findBoard(boardId);
        Reply reply = Reply.create(board, user, dto.getContent());
        replyRepository.save(reply);
    }

    //댓글 수정
    @Transactional
    public void updateReply(Long boardId, Long replyId, ReplyRequestDTO dto, User user) {
        Board board = findBoard(boardId);//게시글 있는지 확인
        Reply reply = findReply(replyId);//댓글 있는지 확인
        reply.validateOwner(user);// 본인이 작성한 댓글 맞는지 확인
        reply.update(dto.getContent());// 위의 조건이 다 해당된다면 수정 가능
    }
    //댓글 삭제
    @Transactional
    public void deleteReply(Long boardId, Long replyId, User user){
        Board board = findBoard(boardId);//게시글 있는지 확인
        Reply reply = findReply(replyId);//댓글 있는지 확인
        reply.validateOwner(user);// 본인이 작성한 댓글 맞는지 확인
        replyRepository.delete(reply);
        }
    }