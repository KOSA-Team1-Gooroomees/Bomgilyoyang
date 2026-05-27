package com.gooroomees.neulbomgil_backend.domain.auth.controller;

import com.gooroomees.neulbomgil_backend.domain.auth.entity.CustomUserDetails;
import com.gooroomees.neulbomgil_backend.domain.auth.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyPageViewController {

    @GetMapping("/mypage")
    public String mypageView(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        User user = userDetails.getUser();
        model.addAttribute("user", user);
        return "auth/mypage";
    }

}

