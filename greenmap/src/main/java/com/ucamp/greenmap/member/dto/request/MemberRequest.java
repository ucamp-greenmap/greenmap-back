package com.ucamp.greenmap.member.dto.request;

import com.ucamp.greenmap.image.domain.Image;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberRequest {
    private Long memberId;
    private boolean isActive;
    private Image image;
    private String email;
    private String nickname;

}
