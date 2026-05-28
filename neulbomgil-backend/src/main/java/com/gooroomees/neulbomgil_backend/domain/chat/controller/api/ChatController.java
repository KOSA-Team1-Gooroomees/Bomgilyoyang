package com.gooroomees.neulbomgil_backend.domain.chat.controller.api;

import com.gooroomees.neulbomgil_backend.domain.auth.entity.User;
import com.gooroomees.neulbomgil_backend.domain.chat.dto.ChatResponseDto;
import com.gooroomees.neulbomgil_backend.domain.chat.dto.ChatRoomResponseDto;
import com.gooroomees.neulbomgil_backend.domain.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "채팅 처리", description = "채팅 관련  API")
@RestController
@RequestMapping("/api/chatrooms")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @Operation(
            summary = "채팅방 시작",
            description = "사용자 채팅방을 생성하거나 기존 채팅방을 반환합니다."
    )
    @PostMapping("/start")
    public ChatRoomResponseDto startChatRoom(@AuthenticationPrincipal User user) {
        return chatService.startChatRoom(user.getUserId());
    }

    @Operation(
            summary = "채팅 메시지 목록 조회",
            description = "특정 채팅방의 메시지 목록을 조회합니다."
    )
    @GetMapping("/{roomId}/message")
    public List<ChatResponseDto> getMessagesByRoomId(@PathVariable Long roomId,@AuthenticationPrincipal User user) {
        return chatService.getMessageByRoomId(roomId, user.getUserId());
    }

    @Operation(
            summary = "관리자의 전체 채팅방 목록 조회",
            description = "전체 채팅방 목록과 마지막 메시지 정보를 조회합니다."
    )
    @GetMapping("/admin")
    public List<ChatRoomResponseDto> getAllChatRooms() {
        return chatService.getAllChatRooms();
    }

//    @Operation(
//            summary = "채팅 메시지 전송",
//            description = "특정 채팅방에 메시지를 저장합니다."
//    )
//    @PostMapping("/{roomId}/message")
//    public ChatResponseDto sendMessage(@PathVariable Long roomId, @AuthenticationPrincipal UserAuth userAuth,
//                                       @RequestBody ChatRequestDto requestDto) {
//        return chatService.saveMessage(roomId,userAuth.getUserId(), requestDto);
//    }

    @Operation(
            summary = "채팅 읽음 처리",
            description = "읽지 않은 채팅 메시지를 읽음 처리합니다."
    )
    @PutMapping("/{roomId}/read")
    public void readMessages(@PathVariable Long roomId, @AuthenticationPrincipal User user) {
        chatService.readMessages(roomId, user.getUserId());
    }

    @Operation(
            summary = "읽지 않은 채팅 여부 확인",
            description = "읽지 않은 채팅 메시지 존재 여부를 반환합니다."
    )
    @GetMapping("/unread")
    public boolean hasUnreadChats(@AuthenticationPrincipal User user) {
       return chatService.hasUnreadChats(user.getUserId());
    }

}
