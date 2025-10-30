package com.ucamp.greenmap.Kakao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // REST API 서버니까 CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/users/login/oauth/start",  // 로그인 시작 경로
                                "/api/users/login/oauth/kakao",   // 카카오 콜백 경로
                                "/**"
                        ).permitAll()  // /error 경로도 허용
                        .anyRequest().authenticated()  // 나머지 요청은 인증 필요
                )
                .formLogin(form -> form.disable())  // 기본 로그인 비활성화
                .httpBasic(basic -> basic.disable());  // 기본 HTTP 인증 비활성화

        return http.build();
    }
}
