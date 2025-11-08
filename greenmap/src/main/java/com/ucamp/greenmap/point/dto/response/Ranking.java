package com.ucamp.greenmap.point.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Ranking {
    private Long memberId;
    private String nickname;
    private Long point;
    private Long carbonSave;
    private String imageUrl;
    private String badgeUrl;
}
