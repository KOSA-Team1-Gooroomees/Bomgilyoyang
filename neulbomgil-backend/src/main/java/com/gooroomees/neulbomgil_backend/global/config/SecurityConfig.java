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
                // .cors(cors -> cors.configurationSource(request -> {
                //    var config = new org.springframework.web.cors.CorsConfiguration();
                //    config.setAllowedOrigins(java.util.List.of("http://localhost:5173")); // 프론트 주소
                //    config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                //    config.setAllowedHeaders(java.util.List.of("*"));
                //    config.setAllowCredentials(true);
                //    return config;
                // }))
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                )
                .authorizeHttpRequests(req -> req
                        //.requestMatchers("/**").permitAll()
                        .requestMatchers("/login", "/signup").permitAll()
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
                .authenticationProvider(authenticationProvider)
                .build();
    }
}
