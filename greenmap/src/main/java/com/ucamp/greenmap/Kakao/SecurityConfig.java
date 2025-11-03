package com.ucamp.greenmap.Kakao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                .sessionManagement(session ->
                        session.sessionFixation().newSession()
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",                    // 홈
                                "/map/**",              // 지도 화면
                                "/place/**",            // 장소 조회 API
                                "/news/**",                // 뉴스
                                "/member/findPw",       // 비밀번호 찾기
                                "/member/signup",       // 회원가입 (혹시 필요하면)
                                "/login/success",
                                "/error",
                                "/favicon.ico",
                                "/oauth2/**",
                                "/login/**",// 로그인 페이지 API
                                "/login/oauth2/**",
                                "https://greenmap-ucamp.netlify.app/**"

                        ).permitAll()
                        .anyRequest().authenticated()
                )

                .oauth2Login(oauth -> oauth
                        .authorizationEndpoint(endpoint ->
                                endpoint.baseUri("/oauth2/authorization")
                        )
                        .redirectionEndpoint(endpoint ->
                                endpoint.baseUri("/login/oauth2/code/*")
                        )
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("https://greenmap-ucamp.netlify.app/login")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                );

        http.addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
