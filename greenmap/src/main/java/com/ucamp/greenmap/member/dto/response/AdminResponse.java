package com.ucamp.greenmap.member.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponse {
    private Boolean result;
    private Long memberId;
}
