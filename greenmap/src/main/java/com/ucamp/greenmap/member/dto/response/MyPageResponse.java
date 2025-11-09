package com.ucamp.greenmap.member.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPageResponse {

    private MemberInfo member;
    private PointInfo point;
    private RankingInfo ranking;

    @Getter
    @Builder
    public static class MemberInfo {
        private Long memberId;
        private String email;
        private String nickname;
        private String imageUrl;
        private String badgeUrl;
    }

    @Getter
    @Builder
    public static class PointInfo {
        private Long point;
        private Long carbonSave;
    }

    @Getter
    @Builder
    public static class RankingInfo {
        private Long rank;
        private Long point;
        private Long carbonSave;
    }
}
