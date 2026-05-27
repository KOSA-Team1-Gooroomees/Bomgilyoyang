package com.gooroomees.neulbomgil_backend.domain.auth.controller.view;

import com.gooroomees.neulbomgil_backend.domain.auth.dto.request.RegisterRequest;
import com.gooroomees.neulbomgil_backend.domain.auth.service.AuthService;
import com.gooroomees.neulbomgil_backend.domain.auth.repository.UserAuthRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import com.gooroomees.neulbomgil_backend.domain.auth.dto.request.LoginRequest;
import com.gooroomees.neulbomgil_backend.domain.auth.dto.response.JwtTokenResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthViewController {

    @Value("${kakao.auth.url}")
    private String kakaoLoginUrl;

    private final AuthService authService;
    private final UserAuthRepository userAuthRepository;

    @GetMapping("/login")
    public String loginView(Model model) {
        model.addAttribute("kakaoLoginUrl", kakaoLoginUrl);
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest request, HttpServletResponse response, Model model) {
        try {
            JwtTokenResponse jwtTokenResponse = authService.login(request);

            // Access Token 쿠키 추가
            Cookie accessCookie = new Cookie("accessToken", jwtTokenResponse.getAccessToken());
            accessCookie.setHttpOnly(true);
            accessCookie.setSecure(true);
            accessCookie.setPath("/");
            accessCookie.setMaxAge(60 * 30); // 30분
            response.addCookie(accessCookie);

            // Refresh Token 쿠키 추가
            Cookie refreshCookie = new Cookie("refreshToken", jwtTokenResponse.getRefreshToken());
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(60 * 60 * 24 * 7); // 7일
            response.addCookie(refreshCookie);

            return "redirect:/mypage";
        } catch (Exception e) {
            model.addAttribute("error", "이메일 또는 비밀번호가 올바르지 않거나 계정이 활성화되지 않았습니다.");
            return "auth/login";
        }
    }

    @GetMapping("/signup")
    public String signupView(Model model) {
        model.addAttribute("signUpForm", new SignUpForm());
        return "auth/register";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute("signUpForm") SignUpForm form, Model model) {
        // 1. 비밀번호 일치 검사
        if (form.getPassword() == null || !form.getPassword().equals(form.getConfirmPassword())) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "auth/register";
        }

        // 2. 이메일 중복 검사
        if (userAuthRepository.findByEmail(form.getEmail()).isPresent()) {
            model.addAttribute("error", "이미 사용 중인 이메일입니다.");
            return "auth/register";
        }

        try {
            RegisterRequest request = RegisterRequest.builder()
                    .email(form.getEmail())
                    .password(form.getPassword())
                    .name(form.getName())
                    .build();
            authService.register(request);
            
            model.addAttribute("message", "회원가입이 완료되었습니다. 이메일 인증 완료 후 로그인 가능합니다.");
            return "auth/login";
        } catch (Exception e) {
            model.addAttribute("error", "회원가입 중 오류가 발생했습니다: " + e.getMessage());
            return "auth/register";
        }
    }

    @Data
    public static class SignUpForm {
        private String name;
        private String email;
        private String password;
        private String confirmPassword;
    }
}

