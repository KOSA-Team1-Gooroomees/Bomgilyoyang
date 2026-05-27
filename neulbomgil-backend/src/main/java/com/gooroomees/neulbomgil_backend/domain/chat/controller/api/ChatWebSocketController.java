package com.gooroomees.neulbomgil_backend.domain.chat.controller.api;

import com.gooroomees.neulbomgil_backend.domain.chat.dto.ChatRequestDto;
import com.gooroomees.neulbomgil_backend.domain.chat.dto.ChatResponseDto;
import com.gooroomees.neulbomgil_backend.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    //클라이언트 SEND 주소:
    //pub/chatrooms/{roomId}/messages
//    @MessageMapping("/chatrooms/{roomId}/messages")
//    public void sendMessage(
//            @DestinationVariable Long roomId,@AuthenticationPrincipal UserAuth userAuth,
//            ChatRequestDto requestDto
//    ){
//
//        log.info("WebSocket 메시지 수신 roomId={}", roomId);
//        log.info("WebSocket 인증 사용자={}", userAuth);
//        log.info("WebSocket 인증 사용자아이디={}", userAuth.getUserId());
//        log.info("WebSocket 메시지={}", requestDto.message());
//
//
//
//
//        ChatResponseDto responseDto = chatService.saveMessage(roomId,userAuth.getUserId(),requestDto);
//
//        //클라이언트 SUBSCRIBE 주소:
//        // /sub/chatRooms/{roomId}
//        messagingTemplate.convertAndSend(
//                "/sub/chatrooms/"+roomId,responseDto
//        );
//    }

        //클라이언트 SEND 주소:
        //pub/chatrooms/{roomId}/messages
        @MessageMapping("/chatrooms/{roomId}/messages")
        public void sendMessage(
                @DestinationVariable Long roomId,@Header("userId") Long userId,/*@AuthenticationPrincipal UserAuth userAuth,*/
                ChatRequestDto requestDto
        ){
            log.info("WebSocket 메시지 수신 roomId={}", roomId);
            log.info("WebSocket 메시지 수신 userId={}", userId);
            log.info("WebSocket 메시지 수신 message={}", requestDto.message());

            ChatResponseDto responseDto = chatService.saveMessage(roomId,userId,requestDto);
            log.info("저장된 메시지 chatId={}", responseDto.chatId());
            //클라이언트 SUBSCRIBE 주소:
            // /sub/chatRooms/{roomId}
            messagingTemplate.convertAndSend(
                    "/sub/chatrooms/"+roomId,responseDto
            );
        }
    }


