package com.gooroomees.neulbomgil_backend.domain.auth.controller;

import com.gooroomees.neulbomgil_backend.domain.auth.dto.request.RegisterRequest;
import com.gooroomees.neulbomgil_backend.domain.auth.entity.User;
import com.gooroomees.neulbomgil_backend.domain.auth.repository.UserRepository;
import com.gooroomees.neulbomgil_backend.domain.auth.service.AuthService;
import com.gooroomees.neulbomgil_backend.domain.auth.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
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
@Slf4j
public class AuthController {

    @Value("${kakao.auth.url}")
    private String kakaoLoginUrl;

    private final AuthService authService;
    private final UserService userService;

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

    @PostMapping("/signup")
    public String signup(@ModelAttribute("signUpForm") RegisterRequest form, Model model) {
        log.info(form.toString());
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
        boolean isDuplicated = userService.findByEmail(email) != null;
        return ResponseEntity.ok(isDuplicated);
    }

    @GetMapping("/api/auth/verify")
    public String verifyUser(@RequestParam("userid") long userId, Model model) {
        User user = userService.findById(userId);
        if (user == null) {
            model.addAttribute("msg", "존재하지 않는 사용자입니다.");
            return "/";
        }

        if (!authService.activateUser(user)) {
            model.addAttribute("msg", "계정 활성화에 실패하였습니다.");
            return "/";
        }

        model.addAttribute("msg", "계정이 활성화되었습니다.");
        model.addAttribute("kakaoLoginUrl", kakaoLoginUrl);
        return "auth/login";
    }

//    @GetMapping("/kakao")
//    public String kakaoLogin(@RequestParam("code") String accessCode, Model model, HttpServletResponse response) {
//        JwtTokenResponse jwtTokenResponse = authService.kakaoOAuthLogin(accessCode, response);
//
//        if (jwtTokenResponse == null)
//            return ResponseEntity.ok(new LoginResponse(null));
//
//        // 리프레시 토큰을 HttpOnly 쿠키에 저장
//        ResponseCookie cookie = ResponseCookie.from("refresh_token", jwtTokenResponse.getRefreshToken())
//                .httpOnly(true)
//                .secure(true) // HTTPS 환경 권장
//                .path("/api/auth/refresh") // 갱신 경로에서만 쿠키 전송
//                .maxAge(604800000)
//                .sameSite("Strict")
//                .build();
//
//        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
//        response.addHeader(HttpHeaders.AUTHORIZATION, jwtTokenResponse.getAccessToken());
//
//        return "/";
//    }
}

