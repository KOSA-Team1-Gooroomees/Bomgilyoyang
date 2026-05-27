package com.gooroomees.neulbomgil_backend.domain.auth.controller;

import com.gooroomees.neulbomgil_backend.domain.auth.dto.request.RegisterRequest;
import com.gooroomees.neulbomgil_backend.domain.auth.service.AuthService;
import com.gooroomees.neulbomgil_backend.domain.auth.repository.UserAuthRepository;
import com.gooroomees.neulbomgil_backend.domain.auth.service.UserAuthService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class AuthController {

    @Value("${kakao.auth.url}")
    private String kakaoLoginUrl;

    private final AuthService authService;
    private final UserAuthRepository userAuthRepository;
    private final UserAuthService userAuthService;

    @GetMapping("/login")
    public String loginView(Model model) {
        model.addAttribute("kakaoLoginUrl", kakaoLoginUrl);
        return "auth/login";
    }

    @GetMapping("/signup")
    public String signupView(Model model) {
        model.addAttribute("signUpForm", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/signup-process")
    public String signup(@ModelAttribute("signUpForm") RegisterRequest form, Model model) {
//        // 1. 비밀번호 일치 검사
//        if (form.getPassword() == null || !form.getPassword().equals(form.getConfirmPassword())) {
//            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
//            return "auth/register";
//        }

//        // 2. 이메일 중복 검사
//        if (userAuthRepository.findByEmail(form.getEmail()).isPresent()) {
//            model.addAttribute("error", "이미 사용 중인 이메일입니다.");
//            return "auth/register";
//        }

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

    @GetMapping("/api/auth/check-email")
    @ResponseBody
    public ResponseEntity<Boolean> checkEmailDuplication(@RequestParam("email") String email) {
        boolean isDuplicated = userAuthService.findByEmail(email) != null;
        return ResponseEntity.ok(isDuplicated);
    }

}

