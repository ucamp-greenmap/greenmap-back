package com.ucamp.greenmap.member.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class RecodeResponse {
    private Long memberId;
    private Integer pointSum;
    private  Integer verifyTimes; // 인증 횟수
    private String mostKind;// 가장 많이 한 활동 네임
    private Integer mostTimes;// 가장 많이 한 활동 횟수
    private Integer timesDiff;// 지난달 대비 인증 횟수
    private Integer pointDiff; // 지난달 대비 획득 포인트



}
