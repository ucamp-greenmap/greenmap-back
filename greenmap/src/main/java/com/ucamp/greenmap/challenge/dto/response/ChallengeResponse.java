package com.ucamp.greenmap.challenge.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeResponse {
    private LocalDateTime updatedAt;
    private String challengeName;
    private String description;
    private Long memberCount;
    private Long deadline;
    private Long success;
    private Long pointAmount;
}
