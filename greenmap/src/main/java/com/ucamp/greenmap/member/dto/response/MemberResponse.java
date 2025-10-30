package com.ucamp.greenmap.member.dto.response;

import com.ucamp.greenmap.image.domain.Image;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponse {
    private Long memberId;
    private boolean isActive;
    private Image image;
    private String email;
    private String nickname;


}
