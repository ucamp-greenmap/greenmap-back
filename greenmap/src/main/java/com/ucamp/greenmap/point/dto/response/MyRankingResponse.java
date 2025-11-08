package com.ucamp.greenmap.point.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyRankingResponse {
    private Long memberId;
    private String nickname;
    private Long memberPoint;
    private Long carbonSave;
    private String imageUrl;
    private Long rank;
}
