package com.ucamp.greenmap.point.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserPointInfo {
    private Long memberId;
    private Long getPoint;
    private Long usedPoint;
    private List<UsedPointLog> logs;
}
