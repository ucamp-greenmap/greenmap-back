package com.ucamp.greenmap.member.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RecodeRequest {
    private Long memberId;
}
