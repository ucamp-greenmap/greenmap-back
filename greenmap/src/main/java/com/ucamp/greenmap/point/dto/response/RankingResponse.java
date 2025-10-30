package com.ucamp.greenmap.point.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RankingResponse {
    private Long memberId;
    private String nickname;
    private Long memberPoint;
    private Long carbonSave;
    private String imageUrl;
    private Long rank;
    private List<Ranking> ranks;
}
