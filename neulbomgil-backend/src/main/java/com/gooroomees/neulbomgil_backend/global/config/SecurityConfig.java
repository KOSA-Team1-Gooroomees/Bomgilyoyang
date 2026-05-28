package com.gooroomees.neulbomgil_backend.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // 웹 보안 활성화, 이 어노테이션이 붙은 클래스는 스프링 시큐리티의 구성 클래스로 사용된다.
@RequiredArgsConstructor
@EnableMethodSecurity // 자바 메소드 단위에서 보안 설정을 적용할 때 사용하는 어노테이션
public class SecurityConfig {
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(request -> {
                    var config = new org.springframework.web.cors.CorsConfiguration();
                    config.setAllowedOrigins(java.util.List.of("http://localhost:5173")); // 프론트 주소
                    config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(java.util.List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> req
                        //websocket연결
                        .requestMatchers("/**").permitAll()
                        .requestMatchers("/api/favorites/**").authenticated()
                        .requestMatchers("/api/map/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/email/**").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        //.requestMatchers()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login-process")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                        .expiredUrl("/")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                //조윤지
//                .authorizeHttpRequests(req -> req
//                        // 인증 없이 허용
//                        .requestMatchers("/api/auth/**").permitAll()
//                        .requestMatchers("/api/email/**").permitAll()
//                        .requestMatchers("/api/map/**").permitAll()
//                        .requestMatchers("/ws/**").permitAll()
//                        .requestMatchers(
//                                "/v3/api-docs/**",
//                                "/swagger-ui/**",
//                                "/swagger-ui.html"
//                        ).permitAll()
//
//                        // 타임리프 정적 리소스 & 페이지
//                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
//                        .requestMatchers("/", "/login", "/signup").permitAll()
//
//                        // 게시글 목록은 비로그인도 허용
//                        .requestMatchers(HttpMethod.GET, "/boards").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/boards/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/boards").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/boards/sort/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/boards/search").permitAll()
//
//                        .requestMatchers(HttpMethod.GET, "/boards/sort/**").permitAll()   // 타임리프 정렬 페이지
//                        .requestMatchers(HttpMethod.GET, "/boards/search").permitAll()    // 타임리프 검색 페이지
//
//                        // 나머지는 로그인 필요
//                        .anyRequest().authenticated()
//                )
                .authenticationProvider(authenticationProvider)
                .build();
    }
}
