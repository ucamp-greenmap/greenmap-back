package com.ucamp.greenmap.challenge.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeRequest {
    private String challengeName;
    private String description;
    private Long memberCount;
    private Long deadline;
    private Long success;
    private Long pointAmount;

}
