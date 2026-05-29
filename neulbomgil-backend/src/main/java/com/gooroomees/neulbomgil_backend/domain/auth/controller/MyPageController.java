package com.gooroomees.neulbomgil_backend.domain.auth.controller;

import com.gooroomees.neulbomgil_backend.domain.auth.dto.request.UserInfoChangeRequest;
import com.gooroomees.neulbomgil_backend.domain.auth.entity.CustomUserDetails;
import com.gooroomees.neulbomgil_backend.domain.auth.entity.User;
import com.gooroomees.neulbomgil_backend.domain.auth.service.AuthService;
import com.gooroomees.neulbomgil_backend.domain.auth.service.CustomUserDetailsService;
import com.gooroomees.neulbomgil_backend.domain.auth.service.UserService;
import com.gooroomees.neulbomgil_backend.domain.favorite.dto.response.FavoriteResponse;
import com.gooroomees.neulbomgil_backend.domain.favorite.service.FavoriteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.gooroomees.neulbomgil_backend.domain.auth.dto.request.PasswordChangeRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final FavoriteService favoriteService;
    private final AuthService authService;
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;

    @GetMapping("/mypage")
    public String mypageView(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            model.addAttribute("error", "로그인 후 접근할 수 있습니다.");
            return "redirect:/login";
        }
        User user = userDetails.getUser();
        model.addAttribute("user", user);

        List<FavoriteResponse> favList = favoriteService.getUserFavoritesWithDetail(user.getUserId());
        model.addAttribute("favList", favList);

        return "auth/mypage";
    }

    @PostMapping("/mypage/info")
    public String changeUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails, UserInfoChangeRequest request, Model model) {
        if (userDetails == null) {
            model.addAttribute("error", "로그인 후 접근할 수 있습니다.");
            return "redirect:/login";
        }

        User user = userDetails.getUser();
        User newUser = userService.changeUserInfo(request, user);
        if (newUser == null) {
            model.addAttribute("error", "잘못된 접근입니다.");
            return "redirect:/login";
        }

        // 1. 현재 수정된 최신 유저 정보를 DB에서 가져오거나 엔티티를 이용해 UserDetails 객체 생성
        UserDetails newUserDetails = customUserDetailsService.loadUserByUsername(newUser.getEmail());

        // 2. 새로운 인증 객체(Authentication) 생성
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                newUserDetails,
                newUserDetails.getPassword(),
                newUserDetails.getAuthorities()
        );

        // 3. SecurityContextHolder에 새로운 인증 객체 설정 (세션 갱신)
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        model.addAttribute("user", newUser);
        model.addAttribute("success", "사용자 정보가 성공적으로 수정되었습니다.");
        return "auth/mypage";
    }

    @PostMapping("/mypage/password")
    public String changePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmNewPassword") String confirmNewPassword,
                                 RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        if (!newPassword.equals(confirmNewPassword)) {
            redirectAttributes.addFlashAttribute("error", "새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
            return "redirect:/mypage";
        }

        if (newPassword.length() < 8 || newPassword.length() > 20) {
            redirectAttributes.addFlashAttribute("error", "새 비밀번호는 8~20자 사이여야 합니다.");
            return "redirect:/mypage";
        }

        User user = userDetails.getUser();
        PasswordChangeRequest changeRequest = PasswordChangeRequest.builder()
                .oldPassword(currentPassword)
                .newPassword(newPassword)
                .build();

        boolean result = authService.changePassword(user, changeRequest);

        if (result) {
            redirectAttributes.addFlashAttribute("message", "비밀번호가 성공적으로 변경되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("error", "현재 비밀번호가 일치하지 않거나 오류가 발생했습니다.");
        }

        return "redirect:/mypage";
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

