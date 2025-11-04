package com.ucamp.greenmap.member.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BasicLoginRequest {
    private String email;
    private String password;
    private String accessToken;
}
