package com.ucamp.greenmap.member.dto.request;

import com.ucamp.greenmap.image.domain.Image;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRequest {
    private Long memberId;
    private boolean isActive;
    private Image image;
    private String email;
    private String nickname;
    private Long ImageId;

}
