package com.gooroomees.neulbomgil_backend.domain.chat.service;


import com.gooroomees.neulbomgil_backend.domain.auth.entity.User;
import com.gooroomees.neulbomgil_backend.domain.auth.repository.UserChatRepository;
import com.gooroomees.neulbomgil_backend.domain.chat.dto.ChatRequestDto;
import com.gooroomees.neulbomgil_backend.domain.chat.dto.ChatResponseDto;
import com.gooroomees.neulbomgil_backend.domain.chat.dto.ChatRoomResponseDto;
import com.gooroomees.neulbomgil_backend.domain.chat.entity.Chat;
import com.gooroomees.neulbomgil_backend.domain.chat.repository.ChatRoomRepository;
import com.gooroomees.neulbomgil_backend.domain.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.gooroomees.neulbomgil_backend.domain.chat.entity.ChatRoom;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final UserChatRepository userAuthRepository;

    public ChatRoomResponseDto startChatRoom(Long userId) {


        User user = userAuthRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));


        ChatRoom chatRoom = chatRoomRepository
                .findChatRoom(user)
                .orElseGet(() -> chatRoomRepository.save(
                        ChatRoom.builder()
                                .user(user)
                                .build()
                ));

        return new ChatRoomResponseDto(
                chatRoom.getRoomId(),
                user.getUserId(),
                user.getName(),
                null,
                null,
                false
        );

    }

    public List<ChatResponseDto> getMessageByRoomId(Long roomId,Long userId) {
        List<Chat> chats = chatRepository.FindMessagesByRoomId(roomId);
        List<ChatResponseDto> chatResponseDtoList = new ArrayList<>();

        for (Chat chat : chats) {
            chatResponseDtoList.add(
                    new ChatResponseDto(
                            chat.getChatId(),
                            roomId,
                            chat.getSender().getUserId(),
                            chat.getMessage(),
                            chat.getCreatedAt(),
                            chat.getReadAt()
                    )
            );
        }

        return chatResponseDtoList;
    }

    public List<ChatRoomResponseDto> getAllChatRooms() {


        List<ChatRoomResponseDto> result =
                chatRoomRepository.findAllChatRoomResponses();


        return result;
    }

    @Transactional
    public ChatResponseDto saveMessage(Long roomId, Long userId, ChatRequestDto requestDto) {

        ChatRoom room = chatRoomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("채팅방이 없습니다."));;

        User sender = userAuthRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        Chat chat = Chat.create(
                room,
                sender,
                requestDto.message()
        );

        Chat savedChat = chatRepository.save(chat);

        room.updateLastMessageAt();

        return new ChatResponseDto(
                savedChat.getChatId(),
                roomId,
                userId,
                savedChat.getMessage(),
                savedChat.getCreatedAt(),
                savedChat.getReadAt()
        );
    }
    @Transactional
    public void readMessages(Long roomId,Long senderId) {
        chatRepository.updateReadAt(roomId,senderId);
    }

    public boolean hasUnreadChats(Long userId) {
       return chatRepository.existsUnreadChatByUserId(userId);
    }
    public ChatRoomResponseDto getChatRoom(Long roomId) {

        return chatRoomRepository.findChatRoomResponse(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방 없음"));
    }
}
