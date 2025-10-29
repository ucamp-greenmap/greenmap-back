package com.ucamp.greenmap.point.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UsePointResponse {

    private Long memberId;
    private Long point;
}
