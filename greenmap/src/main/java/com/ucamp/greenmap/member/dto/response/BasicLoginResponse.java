package com.ucamp.greenmap.member.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasicLoginResponse {
    private Long memberId;
    private String email;
    private String password;
    private String accessToken;
}
