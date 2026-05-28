package com.gooroomees.neulbomgil_backend.domain.auth.controller;

import com.gooroomees.neulbomgil_backend.domain.auth.entity.CustomUserDetails;
import com.gooroomees.neulbomgil_backend.domain.auth.entity.User;
import com.gooroomees.neulbomgil_backend.domain.auth.service.AuthService;
import com.gooroomees.neulbomgil_backend.domain.favorite.dto.response.FavoriteResponse;
import com.gooroomees.neulbomgil_backend.domain.favorite.service.FavoriteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final FavoriteService favoriteService;
    private final AuthService authService;

    @GetMapping("/mypage")
    public String mypageView(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        User user = userDetails.getUser();
        model.addAttribute("user", user);

        List<FavoriteResponse> favList = favoriteService.getUserFavoritesWithDetail(user.getUserId());
        model.addAttribute("favList", favList);

        return "auth/mypage";
    }

    @PostMapping("/mypage/withdraw")
    public String withdrawUser(@AuthenticationPrincipal CustomUserDetails userDetails,
                               @RequestParam("withdrawPassword") String withdrawPassword,
                               HttpServletRequest request,
                               HttpServletResponse response,
                               RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        User user = userDetails.getUser();
        boolean result = authService.withdrawUser(user.getUserId(), withdrawPassword);

        if (result) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }
            redirectAttributes.addFlashAttribute("message", "회원 탈퇴가 완료되었습니다.");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "redirect:/mypage";
        }
    }

}

