package com.ucamp.greenmap.point.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CarbonInfoResponse {
    private Long carbonSave;
    private Long car;
    private Long recycle;
    private Long bike;
    private Long zero;
}
