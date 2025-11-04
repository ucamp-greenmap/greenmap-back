package com.ucamp.greenmap.Kakao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Value("${frontend.url}")
    private String frontendUrl;



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                            .csrf(csrf -> csrf.disable())
                            .cors(cors -> {})
                            .formLogin(form -> form.disable())
                            .httpBasic(basic -> basic.disable())

                            .sessionManagement(session -> session.sessionFixation().newSession())

                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/",
                                                                "/map/**",
                                                                "/place/**",
                                                                "/place",
                                                                "/news/**",
                                                                "/member",
                                                                "/member/me",
                                                                "/member/login",
                                                                "/member/findPw",
                                                                "/member/signup",
                                                                "/login/success",
                                                                "/error",
                                                                "/favicon.ico",
                                                                "/oauth2/**",
                                                                "/login/**",
                                                                "/login/oauth2/**")
                                                .permitAll()
                                                .anyRequest().authenticated())

                            .oauth2Login(oauth -> oauth
                                            .authorizationEndpoint(
                                                            endpoint -> endpoint.baseUri("/oauth2/authorization"))
                                            .redirectionEndpoint(
                                                            endpoint -> endpoint.baseUri("/login/oauth2/code/*"))
                                            .successHandler(oAuth2SuccessHandler)
                                            .failureHandler(oAuth2FailureHandler))

                            .logout(logout -> logout
                                            .logoutUrl("/logout")
                                            .logoutSuccessUrl(frontendUrl + "/login")
                                            .invalidateHttpSession(true)
                                            .clearAuthentication(true));

            http.addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
