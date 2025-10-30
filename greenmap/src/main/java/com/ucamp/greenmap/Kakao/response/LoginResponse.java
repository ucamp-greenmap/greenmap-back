package com.ucamp.greenmap.Kakao.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private String nickname;
    private String email;
    private String accessToken;
    private String refreshToken;
}
