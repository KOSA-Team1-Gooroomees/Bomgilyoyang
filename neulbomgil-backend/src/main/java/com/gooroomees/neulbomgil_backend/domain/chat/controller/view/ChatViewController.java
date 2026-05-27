package com.gooroomees.neulbomgil_backend.domain.chat.controller.view;

import com.gooroomees.neulbomgil_backend.domain.auth.entity.User;
import com.gooroomees.neulbomgil_backend.domain.chat.dto.ChatResponseDto;
import com.gooroomees.neulbomgil_backend.domain.chat.dto.ChatRoomResponseDto;
import com.gooroomees.neulbomgil_backend.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/chatrooms")
@RequiredArgsConstructor
public class ChatViewController {

    private final ChatService chatService;

    @GetMapping("/")
    public String start() {
        return "chat/user";
    }


    @PostMapping("/start")
    public ModelAndView startChatRoom(@AuthenticationPrincipal User user) {
        ChatRoomResponseDto chatRoom =
                chatService.startChatRoom(user.getUserId());

        return new ModelAndView(
                "redirect:/chatrooms/" + chatRoom.roomId() + "/message"
        );
    }

    @GetMapping("/start")
    public ModelAndView startChatRoomGet(
            @AuthenticationPrincipal User user
    ) {
        ChatRoomResponseDto chatRoom =
                chatService.startChatRoom(user.getUserId());

        return new ModelAndView(
                "redirect:/chatrooms/" +
                        chatRoom.roomId() +
                        "/message"
        );
    }


    @GetMapping("/{roomId}/message")
    public ModelAndView chatRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal User user
    ) {

        chatService.readMessages(roomId, user.getUserId());


        List<ChatResponseDto> messages =
                chatService.getMessageByRoomId(roomId, user.getUserId());

        ChatRoomResponseDto room =
                chatService.getChatRoom(roomId);

        ModelAndView mv = new ModelAndView("chat/chat");
        mv.addObject("roomId", roomId);
        mv.addObject("userId", user.getUserId());
        mv.addObject("messages", messages);

         /*
        상대방 이름
    */
        String chatPartnerName;

        if (user.getUserId().equals(room.userId())) {
            chatPartnerName = "관리자";
        } else {
            chatPartnerName = room.name();
        }

        mv.addObject(
                "chatPartnerName",
                chatPartnerName
        );

        return mv;
    }

    @GetMapping("/admin")
    public ModelAndView adminChatRooms() {
        List<ChatRoomResponseDto> chatRooms =
                chatService.getAllChatRooms();


        ModelAndView mv = new ModelAndView("chat/admin");
        mv.addObject("chatRooms", chatRooms);


        return mv;
    }


    @PostMapping("/{roomId}/read")
    @ResponseBody
    public void readMessages(
            @PathVariable Long roomId,
            @AuthenticationPrincipal User user
    ) {
        chatService.readMessages(roomId, user.getUserId());
    }

    @GetMapping("/unread")
    @ResponseBody
    public boolean hasUnreadChats(
            @AuthenticationPrincipal User user
    ) {
        return chatService.hasUnreadChats(
                user.getUserId()
        );
    }
}