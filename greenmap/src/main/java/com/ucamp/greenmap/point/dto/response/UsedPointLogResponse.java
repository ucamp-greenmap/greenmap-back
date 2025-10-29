package com.ucamp.greenmap.point.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UsedPointLogResponse {
    private Long memberId;
    private List<UsedPointLog> usedLogs;
}
