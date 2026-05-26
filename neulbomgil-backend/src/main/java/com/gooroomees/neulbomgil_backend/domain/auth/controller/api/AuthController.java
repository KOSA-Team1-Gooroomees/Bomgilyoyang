package com.gooroomees.neulbomgil_backend.domain.auth.controller.api;

import com.gooroomees.neulbomgil_backend.domain.auth.dto.request.*;
import com.gooroomees.neulbomgil_backend.domain.auth.dto.response.CreateAccessTokenResponse;
import com.gooroomees.neulbomgil_backend.domain.auth.dto.response.JwtTokenResponse;
import com.gooroomees.neulbomgil_backend.domain.auth.dto.response.LoginResponse;
import com.gooroomees.neulbomgil_backend.domain.auth.entity.User;
import com.gooroomees.neulbomgil_backend.domain.auth.service.AuthService;
import com.gooroomees.neulbomgil_backend.domain.auth.service.EmailService;
import com.gooroomees.neulbomgil_backend.domain.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@Tag(name = "인증 및 인가 관리", description = "회원 가입, 로그인, OAuth, 메일 인증용 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final EmailService emailService;
    private final UserService userService;

    @Operation(summary = "회원 가입")
    @PostMapping("/signup")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(summary = "일반 로그인")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@ModelAttribute LoginRequest request, HttpServletResponse response) {
        JwtTokenResponse jwtTokenResponse = authService.login(request);

        // 액세스 토큰을 HttpOnly 쿠키에 저장
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", jwtTokenResponse.getAccessToken())
                .httpOnly(true)
                .secure(true) // HTTPS 환경 권장
                .path("/") // 갱신 경로에서만 쿠키 전송
                .maxAge(Duration.ofMinutes(30))
                .sameSite("Strict")
                .build();

        // 리프레시 토큰을 HttpOnly 쿠키에 저장
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", jwtTokenResponse.getRefreshToken())
                .httpOnly(true)
                .secure(true) // HTTPS 환경 권장
                .path("/") // 갱신 경로에서만 쿠키 전송
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        response.addHeader(HttpHeaders.AUTHORIZATION, jwtTokenResponse.getAccessToken());

        return ResponseEntity.ok(new LoginResponse(jwtTokenResponse.getAccessToken()));
    }

    @Operation(summary = "비밀번호 초기화 메일 발송")
    @PostMapping("/password/reset")
    public ResponseEntity<String> requestPasswordReset(@RequestBody PasswordResetRequest request) {
        try {
            authService.requestPasswordReset(request);

            return ResponseEntity.ok("비밀번호 초기화 메일이 발송되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "비밀번호 재설정")
    @PostMapping("/password/change")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal User user,
                                                 @RequestBody PasswordChangeRequest request) {
        try {
            if (authService.changePassword(user, request))
                return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
            else
                return ResponseEntity.badRequest().body("비밀번호 변경에 실패하였습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader("Authorization") String authHeader,
            @CookieValue(name = "refresh_token") String refreshToken,
            HttpServletResponse response) {
        String accessToken = authHeader.substring(7);

        authService.logout(refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh")
                .maxAge(0) // 쿠키 즉시 만료
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "액세스 토큰 재발급")
    @PostMapping("/refresh")
    public ResponseEntity<?> createAccessToken(@CookieValue(name = "refresh_token", required = false) String refreshToken) {
        // Set-Cookie에서 토큰 빼오는 로직 추가
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(401).body("Refresh Token이 존재하지 않습니다.");
        }

        String newAccessToken = authService.createNewAccessToken(refreshToken);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateAccessTokenResponse(newAccessToken));
    }

    @Operation(summary = "회원가입 인증")
    @GetMapping("/verify")
    public ResponseEntity<String> verifyRegisterEmail(@RequestParam("token") String token) {
        try {
            authService.verifyEmail(token);
            return ResponseEntity.ok("이메일 인증이 완료되었습니다. 창을 닫고 로그인을 진행해주세요.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @Operation(summary = "비밀번호 초기화 인증")
    @GetMapping("/verify/password")
    public ResponseEntity<String> verifyPasswordEmail(@RequestParam("token") String token) {
        try {
            authService.verifyEmail(token);
            return ResponseEntity.ok("이메일 인증이 완료되었습니다. 창을 닫고 로그인을 진행해주세요.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @Operation(summary = "카카오 로그인")
    @GetMapping("/kakao")
    public ResponseEntity<LoginResponse> kakaoLogin(@RequestParam("code") String accessCode, HttpServletResponse response) {
        JwtTokenResponse jwtTokenResponse = authService.kakaoOAuthLogin(accessCode, response);

        if (jwtTokenResponse == null)
            return ResponseEntity.ok(new LoginResponse(null));

        // 리프레시 토큰을 HttpOnly 쿠키에 저장
        ResponseCookie cookie = ResponseCookie.from("refresh_token", jwtTokenResponse.getRefreshToken())
                .httpOnly(true)
                .secure(true) // HTTPS 환경 권장
                .path("/api/auth/refresh") // 갱신 경로에서만 쿠키 전송
                .maxAge(604800000)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.addHeader(HttpHeaders.AUTHORIZATION, jwtTokenResponse.getAccessToken());

        return ResponseEntity.ok(new LoginResponse(jwtTokenResponse.getAccessToken()));
    }

    @Operation(summary = "사용자 삭제")
    @GetMapping("/delete")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal User user) {
        authService.deleteUser(user.getUserId());

        return ResponseEntity.ok("User Removed");
    }
}
