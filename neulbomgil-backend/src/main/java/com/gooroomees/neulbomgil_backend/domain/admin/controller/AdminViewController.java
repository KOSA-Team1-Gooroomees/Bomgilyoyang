package com.gooroomees.neulbomgil_backend.domain.admin.controller;

import com.gooroomees.neulbomgil_backend.domain.admin.dto.AdminUserResponseDto;
import com.gooroomees.neulbomgil_backend.domain.admin.service.AdminService;
import com.gooroomees.neulbomgil_backend.domain.chat.dto.ChatRoomResponseDto;
import com.gooroomees.neulbomgil_backend.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminViewController {

    private final AdminService adminService;
    private final ChatService chatService;

    @GetMapping
    public ModelAndView adminPage() {

        List<ChatRoomResponseDto> chatRooms =
                chatService.getAllChatRooms();

        List<AdminUserResponseDto> users =
                adminService.getUsers();

        ModelAndView mv = new ModelAndView("chat/admin");

       // log.info("user ={}", users);

        mv.addObject("chatRooms", chatRooms);
        mv.addObject("users", users);
        mv.addObject("type", "all");
        mv.addObject("allUserCount",
                adminService.getUsers().size());

        mv.addObject("deletedUserCount",
                adminService.getDeletedUsers().size());

        return mv;
    }

    @GetMapping("/users")
    public ModelAndView getUsers() {
        return adminPage();
    }

    @GetMapping("/users/deleted")
    public ModelAndView getDeletedUsers() {

        List<ChatRoomResponseDto> chatRooms =
                chatService.getAllChatRooms();

        List<AdminUserResponseDto> users =
                adminService.getDeletedUsers();

        ModelAndView mv = new ModelAndView("chat/admin");

        mv.addObject("chatRooms", chatRooms);
        mv.addObject("users", users);
        mv.addObject("type", "deleted");

        mv.addObject("allUserCount",
                adminService.getUsers().size());

        mv.addObject("deletedUserCount",
                adminService.getDeletedUsers().size());

        return mv;
    }

    @PostMapping("/users/{userId}/status")
    public String updateUserStatus(@PathVariable Long userId) {

        adminService.updateUserStatus(userId);

        return "redirect:/admin/users";
    }
}