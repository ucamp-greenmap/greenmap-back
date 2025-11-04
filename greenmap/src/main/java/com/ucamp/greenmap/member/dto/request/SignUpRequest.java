package com.ucamp.greenmap.member.dto.request;

import com.ucamp.greenmap.image.domain.Image;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequest {
    private Long memberId;
    private String email;
    private String password;
    private String nickname;
    private String imageUrl;
}

