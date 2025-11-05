package com.ucamp.greenmap.challenge.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeResponse {
    private String challengeName;
    private String description;
    private Long memberCount;
    private Long deadline;
    private Long success;
    private Long pointAmount;
}
