package com.ucamp.greenmap.Kakao;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        System.out.println("OAuth2 Login Failed: " + exception.getMessage());

        // 로그인 실패 시 프론트로 리다이렉트
        response.sendRedirect("http://localhost:3000/login?error=oauth_failed");
    }
}
