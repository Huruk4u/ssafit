package com.example.ssafit.config;

import com.example.ssafit.filter.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.AntPathMatcher;


/**
 * 웹 보안 규칙 설정
 */
@Configuration
public class WebSecurityConfig {

    // token인증 없이 접근 가능한 URL request
    private static final String[] WHITE_LIST_URL = {
            "/", "/index.html", "/favicon.ico", "/assets/**", "/css/**", "/js/**", "/images/**",
            "/api/v1/auth/**", "/api_auth/authenticate", "/api_user/post/signup",
            "/v2/api-docs", "/v3/api-docs", "/v3/api-docs/**", "/swagger-resources",
            "/swagger-resources/**", "/configuration/ui", "/configuration/security",
            "/swagger-ui/**", "/swagger-ui.html", "/webjars/**", "/api/test/**"
    };

    @Autowired
    @org.springframework.context.annotation.Lazy
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static final AntPathMatcher matcher = new AntPathMatcher();
    /**
     * 보안관련규칙 정의
     * 1. WHITE_LIST_URL에 없는 모든 접근은 인증 절차 없이 접근 불가.
     * 2. User의 역할에 따라 접근 가능 URL 제어
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 정적 리소스 직접 명시
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/favicon.ico",
                                "/assets/**",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()
                        // API 화이트리스트
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/api_auth/authenticate",
                                "/api_user/post/signup",
                                "/v2/api-docs", "/v3/api-docs", "/v3/api-docs/**",
                                "/swagger-resources", "/swagger-resources/**",
                                "/configuration/ui", "/configuration/security",
                                "/swagger-ui/**", "/swagger-ui.html", "/webjars/**",
                                "/api/test/**"
                        ).permitAll()
                        .requestMatchers("/api_admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}