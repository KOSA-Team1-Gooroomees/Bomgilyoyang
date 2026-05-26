package com.gooroomees.neulbomgil_backend.global.config;

import com.gooroomees.neulbomgil_backend.domain.auth.entity.RefreshToken;
import com.gooroomees.neulbomgil_backend.domain.auth.entity.User;
import com.gooroomees.neulbomgil_backend.domain.auth.repository.RefreshTokenRepository;
import com.gooroomees.neulbomgil_backend.domain.auth.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// 모든 HTTP 요청에서 JWT를 검사하는 필터, OncePerRequestFilter를 상속해 요청 당 한 번만 실행됨
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String accessToken = getCookieValue(request, "accessToken");
        String refreshToken = getCookieValue(request, "refreshToken");

        String token = extractToken(request);
        String userEmail;

        try {
            if (accessToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                userEmail = jwtProvider.extractUsername(accessToken);
                User user = (User) this.userDetailsService.loadUserByUsername(userEmail);
                if (jwtProvider.isTokenValid(accessToken, user)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (ExpiredJwtException e) {
            // 액세스 토큰이 만료된 경우 리프레쉬 토큰 검증
            if (refreshToken != null) {
                try {
                    if (jwtProvider.isTokenValid(refreshToken)) {
                        // DB에 저장된 리프레쉬 토큰과 비교
                        RefreshToken dbToken = refreshTokenRepository.findByRefreshToken(refreshToken).orElse(null);

                        if (dbToken != null) {
                            Long loginId = dbToken.getUserId();
                            User user = userRepository.findById(loginId).orElseThrow();

                            // 새 액세스 토큰 발급 및 쿠키 갱신
                            String newAccessToken = jwtProvider.generateAccessToken(user);
                            Cookie cookie = new Cookie("accessToken", newAccessToken);
                            cookie.setHttpOnly(true);
                            cookie.setPath("/");
                            cookie.setMaxAge(60 * 30); // 30분
                            response.addCookie(cookie);

                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    user.getAuthorities()
                            );
                            authToken.setDetails(
                                    new WebAuthenticationDetailsSource().buildDetails(request)
                            );
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        }
                    }
                } catch (ExpiredJwtException ex) {
                    // 리프레쉬 토큰까지 만료된 경우 재로그인 필요 (필터 통과 후 Security 설정에 의해 차단됨)
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
