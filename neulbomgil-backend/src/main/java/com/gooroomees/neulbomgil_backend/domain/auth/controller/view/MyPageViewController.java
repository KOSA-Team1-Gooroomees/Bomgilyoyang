package com.gooroomees.neulbomgil_backend.domain.auth.controller.view;

import com.gooroomees.neulbomgil_backend.domain.auth.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyPageViewController {

    @GetMapping("/mypage")
    public String mypageView(@AuthenticationPrincipal User user, Model model) {
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "auth/mypage";
    }

}

