package com.ucamp.greenmap.member.dto.response;

import com.ucamp.greenmap.image.domain.Image;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SignUpResponse {
    private Long memberId;
    private String email;
    private String password;
    private String nickname;
    private String imageUrl;
    private String accessToken;
}
